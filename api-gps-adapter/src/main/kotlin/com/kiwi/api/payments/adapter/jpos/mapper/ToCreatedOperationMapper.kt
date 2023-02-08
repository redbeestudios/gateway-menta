package com.kiwi.api.payments.adapter.jpos.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ADDITIONAL_INFORMATION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AMOUNT
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AUDIT_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AUTHORIZATION_CODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.CURRENCY
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ICC_DATA
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.MTI
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.NETWORK_INTERNATIONAL_IDENTIFIER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PAN
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PIN_DATA
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PROCESS_CODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.RESPONSE_CODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.RETRIEVAL_REFERENCE_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_IDENTIFICATION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_DATE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_TIME
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TRANSMISSION_DATE_TIME
import com.kiwi.api.payments.adapter.jpos.models.ResponseCode
import com.kiwi.api.payments.adapter.jpos.provider.IsoFieldValueExtractor
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.CreatedOperation.DisplayMessage
import com.kiwi.api.payments.domain.CreatedOperation.DisplayMessage.UseCode.DISPLAY_TEXT
import com.kiwi.api.payments.domain.CreatedOperation.DisplayMessage.UseCode.PRINT_TEXT
import com.kiwi.api.payments.domain.field.Currency
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.extensions.toOffsetDateTime
import com.kiwi.api.payments.shared.error.model.ApplicationError
import kotlinx.coroutines.runBlocking
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component
import com.kiwi.api.payments.domain.field.MTI as MTIType

@Component
class ToCreatedOperationMapper(
    private val isoFieldValueExtractor: IsoFieldValueExtractor
) {

    fun map(isoMsg: ISOMsg): Either<ApplicationError, CreatedOperation> =
        runBlocking {
            either {
                CreatedOperation(
                    mti = isoMsg.getValue(MTI).flatMap { MTIType.from(it) }.bind(),
                    pan = isoMsg.getNullableValue(PAN),
                    processCode = isoMsg.getValue(PROCESS_CODE).bind().let {
                        ProcessCode(
                            transactionType = ProcessCode.TransactionType.from(it.substring(0, 2)).bind(),
                            accountType = ProcessCode.AccountType.from(it.substring(2, 4)).bind(),
                            followMessage = false
                        )
                    },
                    amount = isoMsg.getNullableValue(AMOUNT),
                    transmissionDatetime = isoMsg.getNullableValue(TRANSMISSION_DATE_TIME)?.toOffsetDateTime(),
                    auditNumber = isoMsg.getValue(AUDIT_NUMBER).bind(),
                    terminalDatetime = isoMsg.getNullableValue(TERMINAL_LOCAL_TIME)?.let {
                        isoMsg.getNullableValue(TERMINAL_LOCAL_DATE)?.plus(it)?.toOffsetDateTime()
                    },
                    networkInternationalIdentifier = isoMsg.getValue(NETWORK_INTERNATIONAL_IDENTIFIER).bind(),
                    // MG
                    // retrievalReferenceNumber = isoMsg.getValue(RETRIEVAL_REFERENCE_NUMBER).bind(),
                    retrievalReferenceNumber = isoMsg.getNullableValue(RETRIEVAL_REFERENCE_NUMBER),
                    authorizationCode = isoMsg.getNullableValue(AUTHORIZATION_CODE),
                    responseCode = isoMsg.getValue(RESPONSE_CODE).flatMap {
                        ResponseCode.from(it)
                    }.bind(),
                    terminalIdentification = isoMsg.getValue(TERMINAL_IDENTIFICATION).bind(),
                    commerceNumber = isoMsg.getNullableValue(AMOUNT),
                    cardNationality = null,
                    currency = isoMsg.getNullableValue(CURRENCY)?.let {
                        Currency.from(it).bind()
                    },
                    workingKey = isoMsg.getNullableValue(PIN_DATA),
                    displayMessage = isoMsg.getNullableValue(ADDITIONAL_INFORMATION)?.let {
                        if (it[0].isDigit()) {
                            DisplayMessage(
                                message = it.substring(4, it.substring(0, 1).toInt() + 4),
                                useCode = DisplayMessage.UseCode.from(it.substring(2, 3)).bind()
                            )
                        } else {
                            DisplayMessage(
                                message = it.substring(1, 39),
                                useCode = if (it[0] in listOf('.', ',')) PRINT_TEXT else DISPLAY_TEXT
                            )
                        }
                    },
                    iccData = isoMsg.getNullableValue(ICC_DATA)
                )
            }
        }

    private fun ISOMsg.getValue(field: FieldPosition) =
        isoFieldValueExtractor.extract(this, field)

    private fun ISOMsg.getNullableValue(field: FieldPosition) =
        isoFieldValueExtractor.extractNullable(this, field)
}
