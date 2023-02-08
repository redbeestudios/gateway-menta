package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.AnnulmentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class AnnulmentReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.received.annulment.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : AnnulmentProducerPortOut {

    override fun produce(annulment: Annulment): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, annulment.merchant.id.toString(), annulment.asMessage())
                .log { info("annulment reversal event produced: {}", annulment) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce annulment reversal event: {}", annulment) }
        }

    fun Annulment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
