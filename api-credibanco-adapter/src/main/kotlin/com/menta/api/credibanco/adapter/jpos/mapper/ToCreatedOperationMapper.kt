package com.menta.api.credibanco.adapter.jpos.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.AMOUNT
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.AUDIT_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.AUTHORIZATION_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.CARD_TYPE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.COMMERCE_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.CURRENCY
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.MTI
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.PROCESS_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.RECEIVING_INSTITUTION_IDENTIFICATION_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.RESPONSE_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.RETRIEVAL_REFERENCE_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.SETTLEMENT_DATA
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_IDENTIFICATION
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_DATE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_TIME
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TRANSMISSION_DATE_TIME
import com.menta.api.credibanco.adapter.jpos.models.ResponseCode
import com.menta.api.credibanco.adapter.jpos.provider.IsoFieldValueExtractor
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.field.Currency
import com.menta.api.credibanco.domain.field.ProcessCode
import com.menta.api.credibanco.extensions.toOffsetDateTime
import com.menta.api.credibanco.shared.error.model.ApplicationError
import kotlinx.coroutines.runBlocking
import org.jpos.iso.ISOMsg
import org.springframework.stereotype.Component
import com.menta.api.credibanco.domain.field.MTI as MTIType

@Component
class ToCreatedOperationMapper(
    private val isoFieldValueExtractor: IsoFieldValueExtractor
) {

    fun map(isoMsg: ISOMsg): Either<ApplicationError, CreatedOperation> =
        runBlocking {
            either {
                CreatedOperation(
                    mti = isoMsg.getValue(MTI).flatMap { MTIType.from(it) }.bind(),
                    processCode = isoMsg.getValue(PROCESS_CODE).flatMap { ProcessCode.from(it) }.bind(),
                    amount = isoMsg.getNullableValue(AMOUNT),
                    transmissionDatetime = isoMsg.getNullableValue(TRANSMISSION_DATE_TIME)?.toOffsetDateTime(),
                    auditNumber = isoMsg.getValue(AUDIT_NUMBER).bind(),
                    terminalDatetime = isoMsg.getNullableValue(TERMINAL_LOCAL_TIME)?.let {
                        isoMsg.getNullableValue(TERMINAL_LOCAL_DATE)?.plus(it)?.toOffsetDateTime()
                    },
                    retrievalReferenceNumber = isoMsg.getNullableValue(RETRIEVAL_REFERENCE_NUMBER),
                    authorizationCode = isoMsg.getNullableValue(AUTHORIZATION_CODE),
                    responseCode = isoMsg.getValue(RESPONSE_CODE).flatMap {
                        ResponseCode.from(it)
                    }.bind(),
                    terminalIdentification = isoMsg.getValue(TERMINAL_IDENTIFICATION).bind(),
                    commerceNumber = isoMsg.getNullableValue(COMMERCE_NUMBER),
                    cardNationality = null,
                    currency = isoMsg.getNullableValue(CURRENCY)?.let {
                        Currency.from(it).bind()
                    },
                    workingKey = null,
                    cardType = isoMsg.getValue(CARD_TYPE).bind(),
                    displayMessage = null,
                    receivingInstitutionIdentificationCode = isoMsg.getValue(RECEIVING_INSTITUTION_IDENTIFICATION_CODE).bind(),
                    settlementDataResponse = isoMsg.getNullableValue(SETTLEMENT_DATA)
                )
            }
        }

    private fun ISOMsg.getValue(field: FieldPosition) =
        isoFieldValueExtractor.extract(this, field)

    private fun ISOMsg.getNullableValue(field: FieldPosition) =
        isoFieldValueExtractor.extractNullable(this, field)
}
