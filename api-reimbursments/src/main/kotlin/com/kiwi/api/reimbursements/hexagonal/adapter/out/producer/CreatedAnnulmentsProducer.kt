package com.kiwi.api.reimbursements.hexagonal.adapter.out.producer

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedAnnulmentsRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.queueProducerNotWritten
import com.kiwi.api.reimbursements.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedAnnulmentsProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.reimbursements.created-annulment}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedAnnulmentsRepositoryPortOut {

    override fun save(createdAnnulment: CreatedAnnulment): Either<ApplicationError, Unit> =
        createdAnnulment
            .send()
            .log { info("created annulment event produced: {}", createdAnnulment.id) }

    fun CreatedAnnulment.send(): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, id.toString(), asMessage())
                .log { info("created annulment event produced: {}", it) }
                .let { Unit.right() }
        } catch (e: Exception) {
            queueProducerNotWritten().left()
                .log { error("Error with produce createdAnnulments event: {}", it) }
        }

    private fun CreatedAnnulment.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
