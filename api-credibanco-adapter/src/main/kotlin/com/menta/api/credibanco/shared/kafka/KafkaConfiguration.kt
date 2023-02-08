package com.menta.api.credibanco.shared.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.adapter.RecordFilterStrategy

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaObjectMapper(objectMapper: ObjectMapper) = KafkaObjectMapper(objectMapper)

    @Bean
    fun filterCredibancoTerminals(): RecordFilterStrategy<String?, String?>? =
        RecordFilterStrategy { rec: ConsumerRecord<String?, String?> ->
            rec.headers().lastHeader(FILTER_HEADER)
                ?.let { header -> ACQUIRER != header.value().toString() }
                ?: true
        }

    companion object {
        const val FILTER_HEADER = "ACQUIRER"
        const val ACQUIRER = "CREDIBANCO"
    }
}
