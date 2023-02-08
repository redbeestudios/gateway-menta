package com.kiwi.api.payments.shared.kafka

import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.kafka.support.Acknowledgment

abstract class KafkaMessageConsumer(
    val consumerMessageResolver: ConsumerMessageResolver
) {

    inline fun <reified T> generateConsumerMessage(
        message: String,
        topic: String,
        key: String? = null,
        partitionId: Long? = null,
    ) =
        consumerMessageResolver.resolve<T>(
            message,
            key,
            partitionId,
            topic
        )

    fun <T> ConsumerMessage<T>.consume(
        acknowledgment: Acknowledgment,
        block: (consumerMessage: ConsumerMessage<T>) -> Unit,
    ) =
        logAction { it ->
            it.executeAndAcknowledge(acknowledgment) { block(it) }
        }

    private inline fun <T> T.executeAndAcknowledge(
        acknowledgment: Acknowledgment,
        block: (consumerMessage: T) -> Unit,
    ) =
        block(this)
            .let { acknowledgment.acknowledge() }
            .log { info("executeAndAcknowledge: acknowledge of operation") }

    private inline fun <T> ConsumerMessage<T>.logAction(block: (consumerMessage: ConsumerMessage<T>) -> Unit) {
        with(this) {
            log.info(LOG_START_ACTION, topic, key, partitionId, message)
            block(this)
            log.info(LOG_END_ACTION, topic, key, partitionId, message)
        }
    }

    companion object : CompanionLogger() {
        const val LOG_START_ACTION =
            "Inicio. Manejo de mensaje recibido de kafka. Topic: {}. Key: {}. Partition: {}. Message: {}"
        const val LOG_END_ACTION =
            "Fin. Manejo de mensaje recibido de kafka. Topic: {}. Key: {}. Partition: {}. Message: {}"
    }
}
