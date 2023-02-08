package com.kiwi.api.reversal.shared.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaObjectMapper(objectMapper: ObjectMapper) = KafkaObjectMapper(objectMapper)
}
