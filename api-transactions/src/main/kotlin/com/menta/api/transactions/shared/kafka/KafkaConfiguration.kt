package com.menta.api.transactions.shared.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.transactions.domain.OperationType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.listener.adapter.RecordFilterStrategy
import java.util.Arrays

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaObjectMapper(objectMapper: ObjectMapper) = KafkaObjectMapper(objectMapper)

    @Bean
    fun filterPaymentReverse(): RecordFilterStrategy<String?, String?>? {
        return RecordFilterStrategy { rec: ConsumerRecord<String?, String?> ->
            val which = rec.headers().lastHeader("OPERATION_TYPE")
            which == null || !Arrays.equals(which.value(), OperationType.PAYMENT_REVERSE.name.toByteArray())
        }
    }
}
