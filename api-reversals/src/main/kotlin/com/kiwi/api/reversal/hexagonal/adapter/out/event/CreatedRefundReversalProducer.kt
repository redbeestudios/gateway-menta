package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedRefundProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedRefundReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.created.refund.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedRefundProducerPortOut {

    override fun produce(refund: CreatedRefund): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, refund.id, refund.asMessage())
                .log { info("created refund reversal event produced: {}", refund) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce createdRefund event: {}", refund) }
        }

    fun CreatedRefund.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
