package com.menta.api.banorte.adapter.http.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebFluxConfig {

    @Bean
    fun webClient(): WebClient = WebClient.create()
}
