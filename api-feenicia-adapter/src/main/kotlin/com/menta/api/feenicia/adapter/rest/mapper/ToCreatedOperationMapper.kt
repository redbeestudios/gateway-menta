package com.menta.api.feenicia.adapter.rest.mapper

import com.menta.api.feenicia.adapter.rest.model.FeeniciaResponse
import com.menta.api.feenicia.domain.CreatedOperation
import org.springframework.stereotype.Component

@Component
class ToCreatedOperationMapper() {

    fun map(response: FeeniciaResponse): CreatedOperation =
        with(response) {
            CreatedOperation(
                affiliation = affiliation,
                authnum = authnum,
                responseCode = responseCode,
                transactionId = transactionId,
                merchant = merchant?.let {
                    CreatedOperation.Merchant(
                        id = it.id,
                        address = merchant.address,
                        name = merchant.name,
                        card = merchant.card?.let { card ->
                            CreatedOperation.Merchant.Card(
                                brand = card.brand,
                                first6Digits = card.first6Digits,
                                last4Digits = card.last4Digits,
                                product = card.product
                            )
                        }
                    )
                },
                amount = amount,
                tip = tip,
                currency = currency?.let {
                    CreatedOperation.Currency(
                        id = it.id,
                        description = currency.description
                    )
                },
                issuerBank = issuerBank?.let { CreatedOperation.IssuerBank(it.name) },
                acquirerBank = acquirerBank?.let { CreatedOperation.AcquirerBank(it.name) },
                approved = approved,
                orderId = orderId,
                receiptId = receiptId
            )
        }
}
