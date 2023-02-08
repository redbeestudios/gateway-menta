package com.menta.api.transactions.adapter.`in`.consumer

import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.OperationType.ANNULMENT
import com.menta.api.transactions.domain.OperationType.ANNULMENT_REVERSE
import com.menta.api.transactions.domain.OperationType.PAYMENT
import com.menta.api.transactions.domain.OperationType.PAYMENT_REVERSE
import com.menta.api.transactions.domain.OperationType.REFUND
import com.menta.api.transactions.domain.OperationType.REFUND_REVERSE
import com.menta.api.transactions.domain.ReversalOperation
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.kafka.ConsumerMessageResolver
import com.menta.api.transactions.shared.kafka.KafkaMessageConsumer
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ReversalEventConsumer(
    private val createTransaction: CreateTransactionInPort,
    private val findTransaction: FindTransactionOperationInPort,
    private val toDomainMapper: ToTransactionMapper,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.reversal.created}"],
        groupId = "\${event.group.update}",
        filter = "filterPaymentReverse"
    )
    fun consume(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(value = "OPERATION_TYPE") operationType: String,
        ack: Acknowledgment
    ) =
        generateConsumerMessage<ReversalOperation>(message, topic)
            .consume(ack) {
                it.message.let {
                    it.findTransaction(OperationType.valueOf(operationType))
                        .map { outDatedTransaction ->
                            toDomain(outDatedTransaction, it)
                                .save()
                        }.mapLeft { throw ErrorFindingTransaction() }
                        .log { info("Refund event processed") }
                }
            }

    private fun ReversalOperation.findTransaction(operationType: OperationType) =
        findTransaction.execute(this.operationId, getOriginalOperationType(operationType))

    private fun toDomain(outDatedTransaction: Transaction, reversalOperation: ReversalOperation) =
        toDomainMapper.map(outDatedTransaction, reversalOperation)
            .log { info("transaction builded: {}", it) }

    private fun Transaction.save() =
        createTransaction.execute(this)
            .log { info("transaction saved: {}", it) }

    private fun getOriginalOperationType(operationType: OperationType) =
        when (operationType) {
            PAYMENT_REVERSE -> PAYMENT
            ANNULMENT_REVERSE -> ANNULMENT
            REFUND_REVERSE -> REFUND
            else -> PAYMENT
        }

    companion object : CompanionLogger()
}
