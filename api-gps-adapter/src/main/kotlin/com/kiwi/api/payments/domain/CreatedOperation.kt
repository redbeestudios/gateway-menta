package com.kiwi.api.payments.domain

import arrow.core.Either
import com.kiwi.api.payments.adapter.jpos.models.ResponseCode
import com.kiwi.api.payments.adapter.jpos.provider.leftIfNull
import com.kiwi.api.payments.domain.field.Currency
import com.kiwi.api.payments.domain.field.MTI
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.InvalidCardNationality
import com.kiwi.api.payments.shared.error.model.InvalidUseCode
import java.time.OffsetDateTime

data class CreatedOperation(
    val mti: MTI,
    val pan: String?,
    val processCode: ProcessCode,
    val amount: String?,
    val transmissionDatetime: OffsetDateTime?,
    val auditNumber: String,
    val terminalDatetime: OffsetDateTime?,
    val networkInternationalIdentifier: String,
    val retrievalReferenceNumber: String?,
    val authorizationCode: String?,
    val responseCode: ResponseCode,
    val terminalIdentification: String,
    val commerceNumber: String?,
    val cardNationality: CardNationality?,
    val currency: Currency?,
    val workingKey: String?,
    val displayMessage: DisplayMessage?,
    val iccData: String?
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
        return "CreatedOperation(mti=$mti, processCode=$processCode, amount=$amount, transmissionDatetime=$transmissionDatetime, auditNumber='$auditNumber', terminalDatetime=$terminalDatetime, networkInternationalIdentifier='$networkInternationalIdentifier', retrievalReferenceNumber=$retrievalReferenceNumber, authorizationCode=$authorizationCode, responseCode=$responseCode, terminalIdentification='$terminalIdentification', commerceNumber=$commerceNumber, cardNationality=$cardNationality, currency=$currency, workingKey=$workingKey, displayMessage=$displayMessage)"
    }
}
