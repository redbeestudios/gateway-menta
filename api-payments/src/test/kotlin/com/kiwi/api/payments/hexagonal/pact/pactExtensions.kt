package com.kiwi.api.payments.hexagonal.pact

import au.com.dius.pact.consumer.dsl.PactDslRequestWithPath
import au.com.dius.pact.consumer.dsl.PactDslRequestWithoutPath
import au.com.dius.pact.consumer.dsl.PactDslResponse
import au.com.dius.pact.consumer.dsl.PactDslWithState
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.pactfoundation.consumer.dsl.LambdaDslObject
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import java.text.SimpleDateFormat

fun PactDslWithState.uponReceiving(description: String, body: PactDslRequestWithoutPath.() -> PactDslRequestWithPath): PactDslRequestWithPath {
    return this.uponReceiving(description).run { body() }
}

fun PactDslRequestWithPath.willRespondWith(body: PactDslResponse.() -> PactDslResponse): PactDslResponse {
    return this.willRespondWith().apply { body() }
}

fun PactDslRequestWithoutPath.headers(body: MutableMap<String, String>.() -> Unit): PactDslRequestWithoutPath {
    return this.headers(mutableMapOf<String, String>().apply { body() })
}

fun LambdaDslObject.o(name: String, body: LambdaDslObject.() -> Unit): LambdaDslObject {
    return this.`object`(name, body)
}

fun PactDslResponse.headers(body: MutableMap<String, String>.() -> Unit): PactDslResponse {
    return this.headers(mutableMapOf<String, String>().apply { body() })
}

private val objectMapper: ObjectMapper = Jackson2ObjectMapperBuilder()
    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
    .build()

val webClient =
    ExchangeStrategies.builder()
        .codecs { codecsConfigurer ->
            codecsConfigurer.defaultCodecs()
                .jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
            codecsConfigurer.defaultCodecs()
                .jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
        }.build()
        .let { strategies ->
            WebClient.builder()
                .exchangeStrategies(strategies).build()
        }
