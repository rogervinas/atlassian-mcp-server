package com.rogervinas.tools

import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ToolConfiguration {
    @Bean
    fun toolCallbackProvider(
        jiraTool: JiraTool,
        confluenceTool: AtlassianConfluenceTool,
    ) = MethodToolCallbackProvider.builder()
        .toolObjects(jiraTool, confluenceTool)
        .build()
}