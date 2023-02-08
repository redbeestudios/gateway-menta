package com.kiwi.api.reversal.shared.kafka

import org.springframework.stereotype.Component

@Component
class ConsumerMessageResolver(
    val kafkaObjectMapper: KafkaObjectMapper
) {

    final inline fun <reified T> resolve(
        message: String,
        key: String? = null,
        partitionId: Long? = null,
        topic: String,
    ) =
        ConsumerMessage(
            message = kafkaObjectMapper.deserialize<T>(message),
            key = key,
            partitionId = partitionId,
            topic = topic,
        )
}

data class ConsumerMessage<T>(
    val message: T,
    val key: String?,
    val partitionId: Long?,
    val topic: String,
)
