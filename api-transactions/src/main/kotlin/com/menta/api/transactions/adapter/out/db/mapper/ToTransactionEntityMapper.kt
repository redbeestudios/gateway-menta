package com.menta.api.transactions.adapter.out.db.mapper

import com.menta.api.transactions.domain.Transaction
import org.springframework.stereotype.Component
import com.menta.api.transactions.adapter.out.db.entity.Transaction as TransactionEntity

@Component
class ToTransactionEntityMapper {

    fun map(transaction: Transaction) =
        with(transaction) {
            TransactionEntity(
                transactionId = id,
                type = type.name,
                merchantId = merchantId,
                customerId = customerId,
                terminalId = terminal.id,
                serialCode = terminal.serialCode,
                originalAmount = amount,
                refundedAmount = refundedAmount,
                currency = currency,
                installmentsNumber = installment.number,
                installmentsPlan = installment.plan,
                cardType = card?.type?.name,
                cardMask = card?.mask,
                holderName = card?.holder?.name,
                holderDocument = card?.holder?.document,
                cardBrand = card?.brand,
                cardBank = card?.bank,
                createdDatetime = transaction.operation.datetime
            )
        }
}
