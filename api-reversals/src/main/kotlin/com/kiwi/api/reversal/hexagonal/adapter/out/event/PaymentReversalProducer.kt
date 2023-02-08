package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.PaymentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.received.payment.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : PaymentProducerPortOut {

    override fun produce(payment: Payment): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, payment.merchant.id.toString(), payment.asMessage())
                .log { info("payment reversal event produced: {}", payment) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce payment reversal event: {}", payment) }
        }

    fun Payment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
