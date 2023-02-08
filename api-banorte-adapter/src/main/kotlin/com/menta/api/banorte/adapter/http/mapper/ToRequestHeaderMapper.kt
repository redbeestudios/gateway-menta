package com.menta.api.banorte.adapter.http.mapper

import com.menta.api.banorte.adapter.http.model.HeaderRequest.AGGREGATOR_ID
import com.menta.api.banorte.adapter.http.model.HeaderRequest.AMOUNT
import com.menta.api.banorte.adapter.http.model.HeaderRequest.BANORTE_URL
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CARD_EXP
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CARD_NUMBER
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CAUSE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CMD_TRANS
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CONTROL_NUMBER
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CUSTOMER_REF2
import com.menta.api.banorte.adapter.http.model.HeaderRequest.CUSTOMER_REF5
import com.menta.api.banorte.adapter.http.model.HeaderRequest.EMV_TAGS
import com.menta.api.banorte.adapter.http.model.HeaderRequest.ENTRY_MODE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.INITIAL_DEFERMENT
import com.menta.api.banorte.adapter.http.model.HeaderRequest.MERCHANT_ID
import com.menta.api.banorte.adapter.http.model.HeaderRequest.MODE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.PAGO_MOVIL
import com.menta.api.banorte.adapter.http.model.HeaderRequest.PASSWORD
import com.menta.api.banorte.adapter.http.model.HeaderRequest.PAYMENTS_NUMBER
import com.menta.api.banorte.adapter.http.model.HeaderRequest.PLAN_TYPE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.QPS
import com.menta.api.banorte.adapter.http.model.HeaderRequest.REFERENCE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.RESPONSE_LANGUAGE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.SECURITY_CODE
import com.menta.api.banorte.adapter.http.model.HeaderRequest.SUB_MERCHANT
import com.menta.api.banorte.adapter.http.model.HeaderRequest.TERMINAL_ID
import com.menta.api.banorte.adapter.http.model.HeaderRequest.TRACK_1
import com.menta.api.banorte.adapter.http.model.HeaderRequest.TRACK_2
import com.menta.api.banorte.adapter.http.model.HeaderRequest.USER
import com.menta.api.banorte.domain.CardBrand.AMEX
import com.menta.api.banorte.domain.CommandTransaction.AUTH
import com.menta.api.banorte.domain.CommandTransaction.REFUND
import com.menta.api.banorte.domain.CommandTransaction.REVERSAL
import com.menta.api.banorte.domain.EntryMode.MANUAL
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.domain.ResponseLanguage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ToRequestHeaderMapper(
    @Value("\${api-banorte-adapter.execution-mode}")
    private val executionMode: String
) {
    fun map(operation: Operation): Map<String, String?> =
        providerBasicMapping(operation) +
            provideReverseMapping(operation) +
            providePaymentMapping(operation) +
            provideRefundMapping(operation)

    private fun provideReverseMapping(operation: Operation): Map<String, String?> =
        if (operation.commandTransaction == REVERSAL) {
            mapOf(
                CAUSE.name to "17",
                REFERENCE.name to operation.reference
            )
        } else {
            emptyMap()
        }

    private fun provideRefundMapping(operation: Operation): Map<String, String?> =
        if (operation.commandTransaction == REFUND) {
            mapOf(
                AMOUNT.name to operation.amount,
                CAUSE.name to "17",
                REFERENCE.name to operation.reference
            )
        } else {
            emptyMap()
        }

    private fun providePaymentMapping(operation: Operation): Map<String, String?> =
        if (operation.commandTransaction == AUTH) {
            mapOf(
                AMOUNT.name to operation.amount
            )
        } else {
            emptyMap()
        }

    private fun providerBasicMapping(operation: Operation) =
        mapOf(
            MERCHANT_ID.name to operation.affiliationId,
            USER.name to operation.user.name,
            PASSWORD.name to operation.user.password,
            TERMINAL_ID.name to operation.terminal,
            CMD_TRANS.name to operation.commandTransaction.name,
            PAGO_MOVIL.name to "0",
            ENTRY_MODE.name to operation.entryMode.code,
            MODE.name to executionMode,
            CONTROL_NUMBER.name to operation.controlNumber,
            RESPONSE_LANGUAGE.name to ResponseLanguage.EN.name,
            CUSTOMER_REF2.name to operation.terminal,
            CUSTOMER_REF5.name to operation.aggregator.id,
            SUB_MERCHANT.name to operation.aggregator.childCommerce.id,
            AGGREGATOR_ID.name to operation.aggregator.id,
            BANORTE_URL.name to operation.url,
            TRACK_1.name to if (!isEntryModeManual(operation)) operation.card.track1 else null,
            TRACK_2.name to if (!isEntryModeManual(operation)) operation.card.track2 else null,
            CARD_NUMBER.name to if (isEntryModeManual(operation)) operation.card.pan else null,
            CARD_EXP.name to if (isEntryModeManual(operation)) operation.card.expirationDate else null,
            SECURITY_CODE.name to
                if (operation.entryMode == MANUAL && operation.card.brand == AMEX)
                    operation.card.cvv else null,
            EMV_TAGS.name to if (operation.commandTransaction == AUTH) operation.emvTags else null,
            INITIAL_DEFERMENT.name to "00",
            PAYMENTS_NUMBER.name to operation.installments,
            PLAN_TYPE.name to "0",
            QPS.name to "0"
        )

    fun isEntryModeManual(operation: Operation) = operation.entryMode == MANUAL
}
