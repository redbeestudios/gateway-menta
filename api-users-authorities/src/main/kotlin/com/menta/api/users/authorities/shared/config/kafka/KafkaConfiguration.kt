package com.menta.api.users.authorities.shared.config.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaObjectMapper(objectMapper: ObjectMapper) = KafkaObjectMapper(objectMapper)
}
