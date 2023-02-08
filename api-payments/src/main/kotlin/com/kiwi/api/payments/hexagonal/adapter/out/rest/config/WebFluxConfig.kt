package com.kiwi.api.payments.hexagonal.adapter.out.rest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder

@Configuration
class WebFluxConfig {

    @Bean
    fun webClient(builder: Builder): WebClient = builder.build()
}
