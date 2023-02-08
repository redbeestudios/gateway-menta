package com.menta.api.credibanco.domain

import arrow.core.Either
import com.menta.api.credibanco.adapter.jpos.models.ResponseCode
import com.menta.api.credibanco.adapter.jpos.provider.leftIfNull
import com.menta.api.credibanco.domain.field.Currency
import com.menta.api.credibanco.domain.field.MTI
import com.menta.api.credibanco.domain.field.ProcessCode
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.InvalidCardNationality
import com.menta.api.credibanco.shared.error.model.InvalidUseCode
import java.time.OffsetDateTime

data class CreatedOperation(
    val mti: MTI,
    val processCode: ProcessCode,
    val amount: String?,
    val transmissionDatetime: OffsetDateTime?,
    val auditNumber: String,
    val terminalDatetime: OffsetDateTime?,
    val retrievalReferenceNumber: String?,
    val authorizationCode: String?,
    val responseCode: ResponseCode,
    val terminalIdentification: String,
    val commerceNumber: String?,
    val cardNationality: CardNationality?,
    val currency: Currency?,
    val workingKey: String?,
    val cardType: String,
    val displayMessage: DisplayMessage?,
    val receivingInstitutionIdentificationCode: String,
    val settlementDataResponse: String?
) {

    data class DisplayMessage(
        val useCode: UseCode,
        val message: String,
    ) {
        enum class UseCode(val code: String) {

            DISPLAY_TEXT("22"),
            PRINT_TEXT("29"),
            DONT_PRINT_COMPLIANCE("30"),
            PRINT_FIRM_ADDRESS_ID("31"),
            DONT_PRINT_FIRM_ADDRESS_ID("32");

            companion object {
                private val codes = values().associateBy { it.code }
                fun from(code: String) = codes[code].leftIfNull(InvalidUseCode(code))
            }
        }
    }

    enum class CardNationality(val code: String) {
        NATIONAL("1"),
        INTERNATIONAL("2");

        companion object {
            private val codes: Map<String, CardNationality> = values().associateBy { it.code }

            fun from(code: String): Either<ApplicationError, CardNationality> =
                codes[code].leftIfNull(InvalidCardNationality(code))
        }
    }

    override fun toString(): String {
        return "CreatedOperation(mti=$mti, processCode=$processCode, amount=$amount, transmissionDatetime=$transmissionDatetime, auditNumber='$auditNumber', terminalDatetime=$terminalDatetime, retrievalReferenceNumber=$retrievalReferenceNumber, authorizationCode=$authorizationCode, responseCode=$responseCode, terminalIdentification='$terminalIdentification', commerceNumber=$commerceNumber, cardNationality=$cardNationality, currency=$currency, workingKey=$workingKey, displayMessage=$displayMessage)"
    }
}
