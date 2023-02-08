package com.kiwi.api.reimbursements.hexagonal.adapter.out.producer

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CreatedRefundRepositoryPorOut
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.queueProducerNotWritten
import com.kiwi.api.reimbursements.shared.kafka.KafkaObjectMapper
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedRefundProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.reimbursements.created-refund}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : CreatedRefundRepositoryPorOut {

    override fun save(createdRefund: CreatedRefund): Either<ApplicationError, Unit> =
        createdRefund
            .send()
            .log { info("created refund event produced: {}", createdRefund.id) }

    fun CreatedRefund.send(): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, id.toString(), asMessage())
                .log { info("created refund event produced: {}", it) }
                .let { Unit.right() }
        } catch (e: Exception) {
            queueProducerNotWritten().left()
                .log { error("Error with produce createdRefund event: {}", it) }
        }

    private fun CreatedRefund.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
