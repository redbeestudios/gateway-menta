package com.menta.api.credibanco.domain

import com.menta.api.credibanco.domain.field.CommerceData
import com.menta.api.credibanco.domain.field.Currency
import com.menta.api.credibanco.domain.field.InputMode
import com.menta.api.credibanco.domain.field.MTI
import com.menta.api.credibanco.domain.field.ProcessCode
import com.menta.api.credibanco.domain.field.TerminalData
import java.time.OffsetDateTime

data class Operation(
    val mti: MTI,
    val pan: String?,
    val processCode: ProcessCode,
    val amount: String,
    val transmissionDatetime: OffsetDateTime,
    val auditNumber: String,
    val terminalLocalDatetime: OffsetDateTime,
    val terminalCaptureDate: OffsetDateTime,
    val merchantType: String?,
    val inputMode: InputMode,
    val pointOfServiceConditionCode: String,
    val acquiringInstitutionIdentificationCode: String?,
    val track2: String?,
    val retrievalReferenceNumber: String?,
    val authorizationIdentificationResponse: String?,
    val terminalIdentification: String?,
    val installments: String,
    val commerceCode: String?,
    val commerceData: CommerceData,
    val additionalDataNational: String?,
    val additionalDataPrivate: String,
    val currency: Currency,
    val pin: String?,
    val additionalAmount: String?,
    val terminalData: TerminalData,
    val cardIssuerCategory: String?,
    val additionalInformation: String?, // pendiente 63
    val receivingInstitutionIdenficationCode: String?, // se mapea en la respuesta 100
    val infoText: String?, // pendiente 124
    val networkManagementInformation: String?, // pendiente 125 sera igual que networkInternationalIdentifier en gp?
    val messageAuthenticationCode: String?, // pendiente 128
    val iccData: String?,
    val settlementData: String?,
) {
    override fun toString(): String {
        return "Operation(amount='$amount', transmissionDatetime=$transmissionDatetime, auditNumber='$auditNumber', terminalLocalDatetime=$terminalLocalDatetime, inputMode=$inputMode, retrievalReferenceNumber=$retrievalReferenceNumber, currency=$currency)"
    }
}
