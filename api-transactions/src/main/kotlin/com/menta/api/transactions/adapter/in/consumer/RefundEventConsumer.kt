package com.menta.api.transactions.adapter.`in`.consumer

import arrow.core.Either
import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedRefund
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.domain.OperationType
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
class RefundEventConsumer(
    private val createTransaction: CreateTransactionInPort,
    private val toDomainMapper: ToTransactionMapper,
    private val findTransactionOperationInPort: FindTransactionOperationInPort,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.refund.created}"],
        groupId = "\${event.group.created}"
    )
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedRefund>(message, topic)
            .consume(ack) {
                it.message.let {
                    if (it.isFailed()) {
                        null
                            .log { info("failed refunds cant be saved") }
                    } else {
                        it.findTransaction()
                            .map { outDatedTransaction ->
                                toDomain(outDatedTransaction, it)
                                    .save()
                            }.mapLeft { throw ErrorFindingTransaction() }
                            .log { info("Refund event processed") }
                    }
                }
            }

    private fun CreatedRefund.isFailed() =
        authorization.status.code == FAILED

    private fun CreatedRefund.findTransaction(): Either<ApplicationError, Transaction> =
        findTransactionOperationInPort.execute(UUID.fromString(data.paymentId), OperationType.PAYMENT)
            .logRight { info("original transaction found: {}", it) }

    private fun toDomain(outDatedTransaction: Transaction, refund: CreatedRefund) =
        toDomainMapper.map(outDatedTransaction, refund, OperationType.REFUND)
            .log { info("transaction mapped: {}", it) }

    private fun Transaction.save() =
        createTransaction.execute(this)
            .log { info("transaction saved: {}", it) }

    companion object : CompanionLogger()
}
