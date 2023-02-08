package com.kiwi.api.payments.hexagonal.adapter.`in`.consumer

import com.kiwi.api.payments.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.payments.shared.kafka.KafkaMessageConsumer
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class PaymentEventConsumer(
    private val createOperation: CreateOperationInPort,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.payment.created}"],
        groupId = "\${event.group.persist.payment}"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".persist-retry", dltTopicSuffix = ".persist-dlt"
    )
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedPayment>(message, topic)
            .consume(ack) {
                it.message
                    .save()
                    .log { info("Payment event processed") }
            }

    private fun CreatedPayment.save() = createOperation.execute(this)

    companion object : CompanionLogger()
}
