package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.reversal.shared.kafka.KafkaMessageConsumer
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class CreatedPaymentEventConsumer(
    private val createOperation: CreateOperationInPort,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.created.payment.reversal}"],
        groupId = "\${event.group.persist.payment.reversal}"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".persist-retry", dltTopicSuffix = ".persist-dlt"
    )
    fun consume(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        ack: Acknowledgment
    ) =
        generateConsumerMessage<CreatedPayment>(message, topic)
            .consume(ack) {
                it.message
                    .save()
                    .log { info("Created payment event processed") }
            }

    private fun CreatedPayment.save() = createOperation.execute(this)

    companion object : CompanionLogger()
}
