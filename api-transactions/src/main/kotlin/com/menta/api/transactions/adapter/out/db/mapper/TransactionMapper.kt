package com.menta.api.transactions.adapter.out.db.mapper

import com.menta.api.transactions.domain.Card
import com.menta.api.transactions.domain.CardType
import com.menta.api.transactions.domain.Installments
import com.menta.api.transactions.domain.Operation
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import org.hibernate.internal.util.NullnessHelper.coalesce
import org.springframework.stereotype.Component
import com.menta.api.transactions.adapter.out.db.entity.Operation as OperationEntity

@Component
class TransactionMapper {

    fun map(operation: OperationEntity) =
        with(operation) {
            Transaction(
                id = transaction.transactionId,
                type = TransactionType.valueOf(transaction.type),
                merchantId = transaction.merchantId,
                customerId = transaction.customerId,
                terminal = Transaction.Terminal(
                    id = transaction.terminalId,
                    serialCode = transaction.serialCode
                ),
                currency = transaction.currency,
                installment = Installments(
                    number = transaction.installmentsNumber,
                    plan = transaction.installmentsPlan
                ),
                card = Card(
                    type = transaction.cardType?.let { CardType.valueOf(it) },
                    mask = transaction.cardMask,
                    brand = transaction.cardBrand,
                    bank = transaction.cardBank,
                    holder = Card.Holder(
                        name = transaction.holderName,
                        document = transaction.holderDocument
                    )
                ),
                operation = Operation(
                    id = id,
                    ticketId = ticketId,
                    datetime = datetime,
                    type = OperationType.valueOf(type),
                    status = status,
                    amount = coalesce(transaction.originalAmount, transaction.refundedAmount) ?: "0",
                    acquirerId = acquirerId
                ),
                amount = transaction.originalAmount,
                refundedAmount = transaction.refundedAmount
            )
        }
}
