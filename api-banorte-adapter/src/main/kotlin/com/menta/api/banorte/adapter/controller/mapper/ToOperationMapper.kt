package com.menta.api.banorte.adapter.controller.mapper

import com.menta.api.banorte.adapter.controller.models.OperationRequest
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.domain.CommandTransaction
import com.menta.api.banorte.domain.EntryMode
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.domain.User
import com.menta.api.banorte.domain.provider.AggregatorProvider
import org.springframework.stereotype.Component

@Component
class ToOperationMapper(
    private val aggregatorProvider: AggregatorProvider
) {
    fun map(request: OperationRequest, banorteMerchant: BanorteMerchant, commandTransaction: CommandTransaction) =
        with(request) {
            Operation(
                card = Operation.Card(
                    pan = capture.card.pan,
                    expirationDate = capture.card.expirationDate,
                    cvv = capture.card.cvv,
                    brand = capture.card.brand,
                    track2 = capture.card.track2,
                    track1 = capture.card.track1
                ),
                amount = amount.total,
                controlNumber = trace,
                merchantId = merchant.id,
                entryMode = EntryMode.valueOf(capture.inputMode),
                commandTransaction = commandTransaction,
                emvTags = capture.card.emv?.iccData,
                user = User(
                    name = banorteMerchant.user.username,
                    password = banorteMerchant.user.password
                ),
                installments = installments,
                reference = retrievalReferenceNumber,
                aggregator = getAggregator(),
                terminal = terminal.serialCode,
                affiliationId = banorteMerchant.affiliationId,
                url = banorteMerchant.url
            )
        }

    private fun getAggregator() = aggregatorProvider.provide()
}
