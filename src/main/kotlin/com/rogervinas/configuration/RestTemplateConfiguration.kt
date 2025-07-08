package com.rogervinas.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory


@Configuration
class RestTemplateConfiguration {

    @Bean
    fun atlassianRestTemplate(): RestTemplate {
        return RestTemplate().apply {
            this.uriTemplateHandler = DefaultUriBuilderFactory(System.getenv("ATLASSIAN_URL"))
            this.interceptors.add { request, body, execution ->
                request.headers.setBasicAuth(System.getenv("ATLASSIAN_USER"), System.getenv("ATLASSIAN_TOKEN"))
                execution.execute(request, body)
            }
        }
    }
}
