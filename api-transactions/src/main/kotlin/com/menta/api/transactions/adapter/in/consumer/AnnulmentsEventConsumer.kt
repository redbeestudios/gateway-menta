package com.menta.api.transactions.adapter.`in`.consumer

import arrow.core.Either
import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedAnnulment
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.domain.OperationType.ANNULMENT
import com.menta.api.transactions.domain.OperationType.PAYMENT
import com.menta.api.transactions.domain.StatusCode.FAILED
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.error.model.ApplicationError
import com.menta.api.transactions.shared.kafka.ConsumerMessageResolver
import com.menta.api.transactions.shared.kafka.KafkaMessageConsumer
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AnnulmentsEventConsumer(
    private val createTransaction: CreateTransactionInPort,
    private val toDomainMapper: ToTransactionMapper,
    private val findTransactionOperationInPort: FindTransactionOperationInPort,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.annulment.created}"],
        groupId = "\${event.group.created}"
    )
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedAnnulment>(message, topic)
            .consume(ack) {
                it.message.let {
                    if (it.isFailed()) {
                        null
                            .log { error("Annulment Failed is not save") }
                    } else {
                        it.findTransaction()
                            .map { outDatedTransaction ->
                                toDomain(outDatedTransaction, it)
                                    .save()
                            }.mapLeft { throw ErrorFindingTransaction() }
                            .log { info("Annulments event processed") }
                    }
                }
            }

    private fun CreatedAnnulment.isFailed() =
        authorization.status.code == FAILED

    private fun CreatedAnnulment.findTransaction(): Either<ApplicationError, Transaction> =
        findTransactionOperationInPort.execute(UUID.fromString(data.paymentId), PAYMENT)
            .logRight { info("original transaction found: {}", it) }

    private fun toDomain(outDatedTransaction: Transaction, createdAnnulment: CreatedAnnulment) =
        toDomainMapper.map(outDatedTransaction, createdAnnulment, ANNULMENT)
            .log { info("transaction mapped: {}", it) }

    private fun Transaction.save() =
        createTransaction.execute(this)
            .log { info("transaction saved: {}", it) }

    companion object : CompanionLogger()
}

class ErrorFindingTransaction : RuntimeException("error finding transaction")
