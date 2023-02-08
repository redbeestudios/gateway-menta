package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedPaymentReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.created.payment.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedPaymentProducerPortOut {

    override fun produce(payment: CreatedPayment): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, payment.id, payment.asMessage())
                .log { info("created payment reversal event produced: {}", payment) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce createdPayment event: {}", payment) }
        }

    fun CreatedPayment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
