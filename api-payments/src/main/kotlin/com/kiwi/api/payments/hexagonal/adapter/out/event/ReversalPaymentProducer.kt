package com.kiwi.api.payments.hexagonal.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.PaymentController.Companion.logEither
import com.kiwi.api.payments.hexagonal.application.port.out.ReverseOperationRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.OperationType.PAYMENT_REVERSE
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ReversalPaymentProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.reversal.created}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : ReverseOperationRepositoryPortOut {

    override fun produce(reversalOperation: ReversalOperation): Either<ApplicationError, ReversalOperation> =
        try {
            reversalOperation
                .also { send(it) }
                .right()
        } catch (e: Exception) {
            QueueProducerNotWritten().left()
        }.logEither(
            { error("Error with produce reversal payment created event: {}", reversalOperation) },
            { info("Reversal payment created event produced: {}", reversalOperation) }
        )

    private fun send(reversalOperation: ReversalOperation) {
        ArrayList<Header>()
            .also { it.add(RecordHeader("OPERATION_TYPE", PAYMENT_REVERSE.name.toByteArray())) }
            .let {
                ProducerRecord(
                    topic,
                    null,
                    null,
                    reversalOperation.operationId.toString(),
                    reversalOperation.asMessage(),
                    it
                )
            }
            .let {
                kafkaTemplate.send(it)
            }
    }

    private fun ReversalOperation.asMessage() =
        objectMapper.serialize(this)
}
