package com.kiwi.api.payments.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedPaymentProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.payment.created}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedPaymentProducerPortOut {

    override fun produce(message: CreatedPayment): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, message.id.toString(), message.asMessage())
                .log { info("created payment event produced: {}", message) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce createdPayment event: {}", message) }
        }

    private fun CreatedPayment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
