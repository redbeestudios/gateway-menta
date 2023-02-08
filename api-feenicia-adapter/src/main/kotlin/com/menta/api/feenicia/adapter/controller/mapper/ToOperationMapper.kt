package com.menta.api.feenicia.adapter.controller.mapper

import com.menta.api.feenicia.adapter.controller.models.OperationRequest
import com.menta.api.feenicia.adapter.controller.provider.ItemsProvider
import com.menta.api.feenicia.domain.EntryMode
import com.menta.api.feenicia.domain.EntryMode.EMV
import com.menta.api.feenicia.domain.EntryMode.STRIPE
import com.menta.api.feenicia.domain.FeeniciaMerchant
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import org.springframework.stereotype.Component

@Component
class ToOperationMapper(
    private val itemsProvider: ItemsProvider,
) {
    fun map(request: OperationRequest, operationType: OperationType, feeniciaMerchant: FeeniciaMerchant) =
        with(request) {
            Operation(
                affiliation = feeniciaMerchant.affiliation,
                amount = calculateAmount(),
                cardholderName = capture.card.holder.name,
                items = itemsProvider.provide(calculateAmount()),
                transactionDate = datetime.toInstant().toEpochMilli(),
                contactless = isContactless(entryMode(capture.inputMode)),
                entryMode = entryMode(capture.inputMode),
                emvRequest = getEmvRequestOrNull(entryMode(capture.inputMode), capture.card.emv?.iccData),
                track2 = capture.card.track2,
                transactionId = retrievalReferenceNumber?.toLongOrNull(),
                operationType = operationType,
                deferralData = getDeferralDataOrNull(installments),
                orderId = orderId,
                feeniciaMerchant = feeniciaMerchant
            )
        }

    private fun getDeferralDataOrNull(installment: String?) =
        installment?.let { Operation.DeferralData(payments = installment) }

    private fun OperationRequest.calculateAmount() =
        amount.total.toDouble() / 100

    private fun isContactless(entryMode: EntryMode): Boolean {
        return when (entryMode) {
            EMV, STRIPE -> false
            else -> true
        }
    }

    private fun getEmvRequestOrNull(entryMode: EntryMode, emvRequest: String?): String? {
        return when (entryMode) {
            STRIPE -> null
            else -> emvRequest
        }
    }

    private fun entryMode(inputMode: String) = EntryMode.valueOf(inputMode)
}
