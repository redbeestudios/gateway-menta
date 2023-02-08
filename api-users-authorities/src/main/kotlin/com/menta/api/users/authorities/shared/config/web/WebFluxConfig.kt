package com.menta.api.users.authorities.shared.config.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder

@Configuration
class WebFluxConfig {

    @Bean
    fun webClient(builder: Builder): WebClient = builder.build()
}
