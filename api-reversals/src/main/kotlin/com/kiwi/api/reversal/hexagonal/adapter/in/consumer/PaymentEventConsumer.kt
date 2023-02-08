package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
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
class PaymentEventConsumer(
    private val createPaymentPortIn: CreatePaymentPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.received.payment.reversal}"],
        groupId = "\${event.group.authorize.payment.reversal}"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".authorize-retry", dltTopicSuffix = ".authorize-dlt"
    )
    fun consume(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        ack: Acknowledgment
    ) =
        generateConsumerMessage<Payment>(message, topic)
            .consume(ack) {
                it.message
                    .create()
                    .log { info("Payment event processed") }
            }

    private fun Payment.create() = createPaymentPortIn.execute(this)

    companion object : CompanionLogger()
}
