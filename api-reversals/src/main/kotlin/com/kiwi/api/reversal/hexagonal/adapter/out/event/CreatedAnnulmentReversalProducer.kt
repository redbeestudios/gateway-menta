package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reversal.hexagonal.application.port.out.CreatedAnnulmentProducerPortOut
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.shared.error.model.ApplicationError
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedAnnulmentReversalProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.created.annulment.reversal}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedAnnulmentProducerPortOut {

    override fun produce(annulment: CreatedAnnulment): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, annulment.id, annulment.asMessage())
                .log { info("created annulment reversal event produced: {}", annulment) }
                .let { Unit.right() }
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
                .log { error("Error with produce createdAnnulment event: {}", annulment) }
        }

    fun CreatedAnnulment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
