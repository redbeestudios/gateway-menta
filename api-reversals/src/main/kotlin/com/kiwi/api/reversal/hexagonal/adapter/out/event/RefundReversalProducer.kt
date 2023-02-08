package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.RefundProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class RefundReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.received.refund.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : RefundProducerPortOut {

    override fun produce(refund: Refund): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, refund.merchant.id.toString(), refund.asMessage())
                .log { info("refund reversal event produced: {}", refund) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce refund reversal event: {}", refund) }
        }

    fun Refund.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
