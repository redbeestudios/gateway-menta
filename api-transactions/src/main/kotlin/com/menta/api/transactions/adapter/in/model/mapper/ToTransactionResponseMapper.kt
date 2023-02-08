package com.menta.api.transactions.adapter.`in`.model.mapper

import com.menta.api.transactions.adapter.`in`.model.OperationResponse
import com.menta.api.transactions.adapter.`in`.model.TransactionResponse
import com.menta.api.transactions.domain.Card
import com.menta.api.transactions.domain.Operation
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTransactionResponseMapper {

    fun mapFrom(transaction: Transaction) =
        with(transaction) {
            TransactionResponse(
                id = id,
                type = type,
                operation = map(operation),
                merchantId = merchantId,
                customerId = customerId,
                terminal = TransactionResponse.Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode
                ),
                currency = currency,
                installment = installment,
                amount = amount,
                refundedAmount = refundedAmount,
                card = map(card),
            )
        }
            .log { info("Response mapped: {}", it) }


    private fun map(card: Card?) =
        card?.let {
            with(card) {
                TransactionResponse.Card(
                    type = type,
                    maskedPan = mask,
                    brand = brand,
                    bank = bank,
                    holder = TransactionResponse.Card.Holder(
                        name = holder?.name,
                        document = holder?.document
                    )
                )
            }
        }

    private fun map(operation: Operation) =
        with(operation) {
            OperationResponse(
                id = id,
                ticketId = ticketId,
                datetime = datetime,
                status = status,
                type = type,
                amount = amount,
                acquirerId = acquirerId
            )
        }

    companion object : CompanionLogger()
}
