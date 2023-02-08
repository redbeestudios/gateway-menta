package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
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
class AnnulmentEventConsumer(
    private val createAnnulmentPortIn: CreateAnnulmentPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.received.annulment.reversal}"],
        groupId = "\${event.group.authorize.annulment.reversal}"
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
        generateConsumerMessage<Annulment>(message, topic)
            .consume(ack) {
                it.message
                    .create()
                    .log { info("Annulment event processed") }
            }

    private fun Annulment.create() = createAnnulmentPortIn.execute(this)

    companion object : CompanionLogger()
}
