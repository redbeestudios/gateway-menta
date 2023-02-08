package com.menta.api.credibanco.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebFluxConfig {

    @Bean
    fun webClient(): WebClient = WebClient.create()
}
