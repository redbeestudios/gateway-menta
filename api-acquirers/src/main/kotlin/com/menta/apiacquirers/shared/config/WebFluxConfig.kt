package com.menta.apiacquirers.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient.Builder

@Configuration
class WebFluxConfig {

    @Bean
    fun webClient(webClientBuilder: Builder) = webClientBuilder.build()
}
