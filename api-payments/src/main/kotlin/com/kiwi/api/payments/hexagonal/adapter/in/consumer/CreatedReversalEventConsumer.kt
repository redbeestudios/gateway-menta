package com.kiwi.api.payments.hexagonal.adapter.`in`.consumer

import com.kiwi.api.payments.hexagonal.application.port.`in`.UpdateStatusOperationPortIn
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.payments.shared.kafka.KafkaMessageConsumer
import com.kiwi.api.payments.shared.util.kafka.evaluateResponse
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class CreatedReversalEventConsumer(
    private val updateStatusOperationPortIn: UpdateStatusOperationPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {
    @KafkaListener(
        topics = ["\${event.topic.reversal.created}"],
        groupId = "\${event.group.update.reversal}",
        filter = "filterPaymentReverse"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".persist-retry", dltTopicSuffix = ".persist-dlt"
    )
    fun consume(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(value = "OPERATION_TYPE") operationType: String,
        ack: Acknowledgment
    ) =
        log.benchmark("Created Reversal for Payment") {
            generateConsumerMessage<ReversalOperation>(message, topic)
                .consume(ack) {
                    it.message
                        .reverse()
                        .evaluateResponse()
                        .log { info("Created Reversal processed") }
                }
        }

    private fun ReversalOperation.reverse() = updateStatusOperationPortIn.execute(this)

    companion object : CompanionLogger()
}
