package com.rogervinas.tools

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

data class ConfluencePage(
    val title: String,
    val id: String,
)

@Service
class AtlassianConfluenceTool(private val restTemplate: RestTemplate) {

    @Tool(
        description = "get all pages of a confluence space",
    )
    @Cacheable("confluenceSpacePagesCache")
    fun confluenceSpacePages(
        @ToolParam(description = "Confluence space name") space: String
    ): List<ConfluencePage> {
        println("confluenceSpacePages: $space")
        val url = "/wiki/rest/api/space/$space?expand=homepage"
        val homepage = restTemplate.getForObject(url, JsonNode::class.java)!!.get("homepage")
        val pageId = homepage.get("id").asText()
        val pageTitle = homepage.get("title").asText()
        return listOf(ConfluencePage(pageTitle, pageId)) + confluenceChildPages(pageId, pageTitle)
    }

    private fun confluenceChildPages(pageId: String, parentPageTitle: String): List<ConfluencePage> {
        println("confluenceChildPages: $pageId $parentPageTitle")
        val url = "/wiki/rest/api/content/$pageId/child/page"
        val pages = mutableListOf<ConfluencePage>()
        restTemplate.getForObject(url, JsonNode::class.java)?.get("results")?.asIterable()?.forEach {
            val pageTitle = "$parentPageTitle > ${it.get("title").asText()}"
            val pageId = it.get("id").asText()
            pages.add(ConfluencePage(pageTitle, pageId))
            pages.addAll(confluenceChildPages(pageId, pageTitle))
        }
        return pages
    }

    @Tool(
        description = "get confluence page content",
    )
    @Cacheable("confluencePageContentCache")
    fun confluencePageContent(
        @ToolParam(description = "Confluence page id") pageId: String
    ): String {
        val url = "/wiki/rest/api/content/$pageId?expand=body.export_view"
        val page = restTemplate.getForObject(url, JsonNode::class.java)!!
        val title = page.get("title").asText()
        println("confluencePageContent: $pageId $title")
        return page.get("body").get("export_view").get("value").asText()
    }

    fun confluenceSearch(space: String, search: String): JsonNode {
        println("confluenceSearch: $space $search")
        val uri = UriComponentsBuilder.fromUriString("/wiki/rest/api/content/search")
            .queryParam(
                "cql",
                "space=$space AND type=page AND text ~ \"$search\""
            )
            .queryParam("excerpt", "highlight")
            .queryParam("limit", 25)
            .build()
            .toUri()
        return restTemplate.getForObject(uri, JsonNode::class.java)!!
    }
}

