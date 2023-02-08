package com.menta.api.transactions.adapter.`in`.consumer.mapper

import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedAnnulment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedBillPayment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedPayment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedRefund
import com.menta.api.transactions.domain.Card
import com.menta.api.transactions.domain.Card.Holder
import com.menta.api.transactions.domain.CardType
import com.menta.api.transactions.domain.Installments
import com.menta.api.transactions.domain.Operation
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.Payment
import com.menta.api.transactions.domain.ReversalOperation
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.StatusCode.APPROVED
import com.menta.api.transactions.domain.StatusCode.REVERSED
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.Transaction.Terminal
import com.menta.api.transactions.domain.TransactionType.ACQUIRER
import com.menta.api.transactions.domain.TransactionType.BILL
import com.menta.api.transactions.domain.provider.IdProvider
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToTransactionMapper(
    private val idProvider: IdProvider
) {
    fun map(
        outDatedTransaction: Transaction,
        refund: CreatedRefund,
        operationType: OperationType
    ) =
        with(outDatedTransaction) {
            Transaction(
                id = id,
                type = type,
                merchantId = merchantId,
                customerId = customerId,
                currency = currency,
                amount = amount,
                refundedAmount = refund.data.amount.total,
                operation = Operation(
                    id = UUID.fromString(refund.id),
                    ticketId = refund.ticketId,
                    datetime = refund.data.datetime,
                    type = operationType,
                    status = refund.authorization.status.code,
                    amount = refund.data.amount.total,
                    acquirerId = refund.authorization.retrievalReferenceNumber
                ),
                installment = installment,
                card = card,
                terminal = Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode
                )
            )
        }

    fun map(
        outDatedTransaction: Transaction,
        reversalOperation: ReversalOperation
    ) =
        with(outDatedTransaction) {
            Transaction(
                id = id,
                type = type,
                merchantId = merchantId,
                customerId = customerId,
                currency = currency,
                amount = amount,
                refundedAmount = refundedAmount,
                operation = Operation(
                    id = reversalOperation.operationId,
                    ticketId = outDatedTransaction.operation.ticketId,
                    datetime = reversalOperation.datetime,
                    type = operation.type,
                    status = REVERSED,
                    amount = reversalOperation.amount.total,
                    acquirerId = reversalOperation.acquirerId
                ),
                installment = installment,
                card = card,
                terminal = Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode
                )
            )
        }

    fun map(
        outDatedTransaction: Transaction,
        annulments: CreatedAnnulment,
        operationType: OperationType
    ) =
        with(outDatedTransaction) {
            Transaction(
                id = id,
                type = type,
                merchantId = merchantId,
                customerId = customerId,
                terminal = Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode
                ),
                currency = currency,
                amount = amount,
                refundedAmount = annulments.data.amount.total,
                operation = Operation(
                    id = UUID.fromString(annulments.id),
                    ticketId = annulments.ticketId,
                    datetime = annulments.data.datetime,
                    type = operationType,
                    status = annulments.authorization.status.code,
                    amount = annulments.data.amount.total,
                    acquirerId = annulments.authorization.retrievalReferenceNumber
                ),
                installment = installment,
                card = card,
            )
        }

    fun map(
        outDatedTransaction: Transaction,
        createdBillPayment: CreatedBillPayment,
        operationType: OperationType
    ) =
        with(outDatedTransaction) {
            Transaction(
                id = id,
                type = type,
                merchantId = merchantId,
                customerId = customerId,
                terminal = Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode
                ),
                currency = currency,
                amount = amount,
                refundedAmount = createdBillPayment.operation.amount,
                operation = Operation(
                    id = createdBillPayment.operation.id,
                    ticketId = createdBillPayment.operation.ticketId,
                    datetime = createdBillPayment.operation.datetime,
                    type = operationType,
                    status = APPROVED,
                    amount = createdBillPayment.operation.amount,
                    acquirerId = null
                ),
                installment = installment,
                card = card,
            )
        }

    fun map(
        payment: CreatedPayment,
        operationType: OperationType
    ) =
        with(payment) {
            Transaction(
                id = idProvider.provide(),
                type = payment.origin ?: ACQUIRER,
                merchantId = data.merchant.id,
                customerId = data.customer.id,
                terminal = Terminal(
                    id = data.terminal.id,
                    serialCode = data.terminal.serialCode
                ),
                currency = data.amount.currency,
                amount = data.amount.total,
                refundedAmount = null,
                operation = Operation(
                    id = UUID.fromString(id),
                    ticketId = ticketId,
                    datetime = data.datetime,
                    type = operationType,
                    status = payment.authorization.status.code,
                    amount = data.amount.total,
                    acquirerId = authorization.retrievalReferenceNumber
                ),
                installment = getInstallments(this.data.installments),
                card = getCard(this.data.capture.card),

            )
        }

    fun map(
        billPayment: CreatedBillPayment,
        operationType: OperationType
    ) =
        with(billPayment) {
            Transaction(
                id = idProvider.provide(),
                type = BILL,
                merchantId = merchantId,
                customerId = customerId,
                terminal = Terminal(
                    id = terminalId,
                    serialCode = serialCode
                ),
                currency = currency,
                amount = operation.amount,
                refundedAmount = null,
                operation = Operation(
                    id = operation.id,
                    ticketId = operation.ticketId,
                    datetime = operation.datetime,
                    type = operationType,
                    status = APPROVED,
                    amount = operation.amount,
                    acquirerId = null
                ),
                installment = getInstallments(installmentNumber),
                card = Card(
                    type = card?.let { it.type?.let { t -> CardType.valueOf(t) } },
                    mask = card?.mask,
                    brand = card?.brand,
                    bank = card?.bank,
                    holder = Holder(
                        name = card?.holder?.name,
                        document = card?.holder?.document,
                    )
                )
            )
        }

    private fun getCard(card: Payment.Capture.Card) =
        with(card) {
            Card(
                type = CardType.valueOf(type),
                mask = pan,
                brand = brand,
                bank = bank ?: "",
                holder = Holder(
                    name = holder.name,
                    document = holder.identification?.number ?: ""
                )
            )
        }

    private fun getInstallments(installment: String) =
        Installments(
            number = installment,
            plan = "plan" // TODO: ver que hacer con este campo
        )
}
