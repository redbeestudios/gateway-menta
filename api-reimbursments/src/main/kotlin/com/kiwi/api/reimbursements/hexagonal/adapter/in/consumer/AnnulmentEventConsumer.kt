package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.consumer

import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.reimbursements.shared.kafka.KafkaMessageConsumer
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class AnnulmentEventConsumer(
    private val createOperation: CreateOperationInPort,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.reimbursements.created-annulment}"],
        groupId = "\${event.group.persist.annulment}"
    )
    @RetryableTopic(attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".persist-retry", dltTopicSuffix = ".persist-dlt")
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedAnnulment>(message, topic)
            .consume(ack) {
                it.message
                    .save()
                    .log { info("Annulment event processed") }
            }

    private fun CreatedAnnulment.save() = createOperation.execute(this)

    companion object : CompanionLogger()
}
