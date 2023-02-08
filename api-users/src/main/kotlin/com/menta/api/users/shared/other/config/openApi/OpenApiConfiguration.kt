package com.menta.api.users.shared.other.config.openApi

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver =
        ModelResolver(objectMapper)
}
