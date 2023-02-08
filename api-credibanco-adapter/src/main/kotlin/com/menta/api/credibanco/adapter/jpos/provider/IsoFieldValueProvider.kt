package com.menta.api.credibanco.adapter.jpos.provider

import com.menta.api.credibanco.adapter.jpos.models.FieldPosition
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.ACQUIRER_INSTITUTION_IDENTIFICATION_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.ADDITIONAL_DATA_ISO
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.ADDITIONAL_DATA_PRIVATE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.AMOUNT
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.AUDIT_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.CARD_TYPE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.COMMERCE_NAME_LOCATION
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.COMMERCE_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.CURRENCY
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.INFO_TEXT
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.INPUT_MODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.MERCHANT_TYPE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.MTI
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.PIN_DATA
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.POINT_OF_SERVICE_CONDITION_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.PROCESS_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.RECEIVING_INSTITUTION_IDENTIFICATION_CODE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.RETRIEVAL_REFERENCE_NUMBER
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.SETTLEMENT_DATA
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_CAPTURE_DATE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_DATA
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_IDENTIFICATION
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_DATE
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_TIME
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TRACK_2
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition.TRANSMISSION_DATE_TIME
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.domain.field.CommerceData
import com.menta.api.credibanco.extensions.toAcquirerFormat
import com.menta.api.credibanco.extensions.toDateInAcquirerFormat
import com.menta.api.credibanco.extensions.toTimeInAcquirerFormat
import org.springframework.stereotype.Component

@Component
class IsoFieldValueProvider {
    fun provide(position: FieldPosition, operation: Operation): String? =
        providers[position]?.provide(operation)

    companion object {
        private val providers: ValueProviders =
            mapOf(
                MTI to ValueProvider { it.mti.code },
                PROCESS_CODE to ValueProvider { it.processCode.code },
                AMOUNT to ValueProvider { it.amount },
                TRANSMISSION_DATE_TIME to ValueProvider { it.terminalLocalDatetime.toAcquirerFormat() },
                AUDIT_NUMBER to ValueProvider { it.auditNumber },
                TERMINAL_LOCAL_TIME to ValueProvider { it.terminalLocalDatetime.toTimeInAcquirerFormat() },
                TERMINAL_LOCAL_DATE to ValueProvider { it.terminalLocalDatetime.toDateInAcquirerFormat() },
                TERMINAL_CAPTURE_DATE to ValueProvider { it.terminalCaptureDate.toDateInAcquirerFormat() },
                MERCHANT_TYPE to ValueProvider { it.merchantType },
                INPUT_MODE to ValueProvider { it.inputMode.toAcquirerFormat() },
                POINT_OF_SERVICE_CONDITION_CODE to ValueProvider { it.pointOfServiceConditionCode },
                ACQUIRER_INSTITUTION_IDENTIFICATION_CODE to ValueProvider { it.acquiringInstitutionIdentificationCode },
                TRACK_2 to ValueProvider { it.track2?.replace("d|D".toRegex(), "=") },
                RETRIEVAL_REFERENCE_NUMBER to ValueProvider { it.retrievalReferenceNumber },
                TERMINAL_IDENTIFICATION to ValueProvider { it.terminalIdentification },
                COMMERCE_NUMBER to ValueProvider { it.installments + "  " + it.commerceCode },
                COMMERCE_NAME_LOCATION to ValueProvider { it.commerceData.getNameLocation() },
                ADDITIONAL_DATA_ISO to ValueProvider { it.additionalDataNational },
                ADDITIONAL_DATA_PRIVATE to ValueProvider { it.additionalDataPrivate.padEnd(27, '0') },
                CURRENCY to ValueProvider { it.currency.code },
                PIN_DATA to ValueProvider { it.pin },
                TERMINAL_DATA to ValueProvider
                {
                    with(it.terminalData) {
                        ownerFiid + logicalNetwork + timeOffset + terminalId
                    }
                },
                CARD_TYPE to ValueProvider { it.cardIssuerCategory },
                RECEIVING_INSTITUTION_IDENTIFICATION_CODE to ValueProvider { it.receivingInstitutionIdenficationCode },
                INFO_TEXT to ValueProvider { it.infoText },
                SETTLEMENT_DATA to ValueProvider { it.settlementData }
            )
    }
}

typealias ValueProviders = Map<FieldPosition, ValueProvider>

fun interface ValueProvider {
    fun provide(operation: Operation): String?
}

private fun String.getFieldLength() =
    count().toString().padStart(3, '0')

private fun buildVariableLengthField(field: String, position: String) =
    field.getFieldLength() + position + field

private fun CommerceData.getNameLocation(): String =
    (name?.take(22)?.padEnd(22, ' ') ?: "                      ") +
        (terminalCity.code + terminalCity.name).take(13).padEnd(13, ' ') +
        terminalCity.code.take(2).padEnd(3) + country.name.take(2)
