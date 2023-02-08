package com.kiwi.api.payments.domain

import com.kiwi.api.payments.domain.field.Aggregator
import com.kiwi.api.payments.domain.field.AppVersion
import com.kiwi.api.payments.domain.field.Currency
import com.kiwi.api.payments.domain.field.InputMode
import com.kiwi.api.payments.domain.field.Installments
import com.kiwi.api.payments.domain.field.MTI
import com.kiwi.api.payments.domain.field.PreviousTransactionInputMode
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.domain.field.TerminalFeature
import java.time.OffsetDateTime

// TODO: hablar con santi por el tema del vault del pin
// TODO: Ver con GP en tema del fallback en la anulacion y devolucion

// TODO: Ver con Zully el campo 59

data class Operation(
    val mti: MTI,
    val pan: String?,
    val processCode: ProcessCode,
    val amount: String,
    val transmissionDatetime: OffsetDateTime,
    val auditNumber: String,
    val terminalLocalDatetime: OffsetDateTime,
    val expirationDate: String?,
    val inputMode: InputMode,
    val previousTransactionInputMode: PreviousTransactionInputMode?,
    val cardSequenceNumber: String?,
    val ksn: String?,
    val iccData: String?,
    val pin: String?,
    val networkInternationalIdentifier: String,
    val pointOfServiceConditionCode: String,
    val track2: String?,
    val retrievalReferenceNumber: String?,
    val terminalIdentification: String,
    val commerceCode: String,
    val track1: String?,
    val track1Read: Boolean?,
    val installments: Installments,
    val currency: Currency,
    val additionalAmount: String?,
    val cvv: String?,
    val appVersion: AppVersion,
    val ticket: String,
    val batch: String,
    val terminalFeature: TerminalFeature,
    val aggregator: Aggregator?
) {
    override fun toString(): String {
        return "Operation(mti=$mti, processCode=$processCode, amount='$amount', transmissionDatetime=$transmissionDatetime, auditNumber='$auditNumber', terminalLocalDatetime=$terminalLocalDatetime, inputMode=$inputMode, previousTransactionInputMode=$previousTransactionInputMode, cardSequenceNumber=$cardSequenceNumber, networkInternationalIdentifier='$networkInternationalIdentifier', pointOfServiceConditionCode='$pointOfServiceConditionCode', retrievalReferenceNumber=$retrievalReferenceNumber, terminalIdentification='$terminalIdentification', commerceCode='$commerceCode', track1Read=$track1Read, installments=$installments, currency=$currency, additionalAmount=$additionalAmount, appVersion=$appVersion, ticket='$ticket', batch='$batch', terminalFeature=$terminalFeature, aggregator=${aggregator ?: "none"})"
    }
}
