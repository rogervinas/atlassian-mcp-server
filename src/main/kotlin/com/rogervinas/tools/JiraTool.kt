package com.rogervinas.tools

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class JiraTool(private val restTemplate: RestTemplate) {

    @Tool(
        description = "get summary of a Jira ticket",
    )
    @Cacheable("jiraTicketSummaryCache")
    fun jiraTicketSummary(
        @ToolParam(description = "id of the Jira ticket") id: String
    ): String {
        println("jiraTicketSummary: $id")
        val result = restTemplate.getForObject(
            "/si/jira.issueviews:issue-html/$id/$id.html",
            String::class.java
        )!!
        println("jiraTicketSummary: $id = ${result.substring(0, 20)}")
        return result
    }

    @Tool(
        description = "get all Jira tickets",
    )
    @Cacheable("jiraTicketsCache")
    fun jiraTickets(
        @ToolParam(description = "Jira project") project: String,
        @ToolParam(description = "Jira tickets from date") fromDate: LocalDate,
        @ToolParam(description = "Jira tickets to date") toDate: LocalDate,
    ): List<String> {
        try {
            println("jiraTickets $project $fromDate - $toDate")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDateStr = fromDate.format(formatter)
            val toDateStr = toDate.format(formatter)
            val uri = UriComponentsBuilder.fromUriString("/rest/api/3/search/jql")
                .queryParam(
                    "jql",
                    "project=\"$project\" AND created >= \"$fromDateStr\" AND created <= \"$toDateStr\" ORDER BY created ASC"
                )
                .queryParam("startAt", "0")
                .queryParam("maxResults", 1000)
                .queryParam("fields", "key")
                .build()
                .toUriString()
            val headers = HttpHeaders()
            headers.add("Accept", "application/json")
            val entity = HttpEntity<String>(headers)
            val results = restTemplate.exchange(uri, GET, entity, JsonNode::class.java).body!!
            val result = results.get("issues").map { it.get("key").asText() }.toList()
            println("jiraTickets: $result")
            return result
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }
}
