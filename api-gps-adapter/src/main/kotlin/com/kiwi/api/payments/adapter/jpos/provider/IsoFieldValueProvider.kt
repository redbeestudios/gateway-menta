package com.kiwi.api.payments.adapter.jpos.provider

import com.kiwi.api.payments.adapter.jpos.models.FieldPosition
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ADDITIONAL_AMOUNTS
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ADDITIONAL_DATA_ISO
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ADDITIONAL_DATA_PRIVATE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AMOUNT
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.APP_VERSION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.AUDIT_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.CARD_SEQUENCE_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.COMMERCE_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.CURRENCY
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.EXPIRATION_DATE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.ICC_DATA
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.INPUT_MODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.KSN
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.MTI
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.NETWORK_INTERNATIONAL_IDENTIFIER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PAN
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PIN_DATA
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.POINT_OF_SERVICE_CONDITION_CODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.PROCESS_CODE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.RETRIEVAL_REFERENCE_NUMBER
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_IDENTIFICATION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_DATE
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TERMINAL_LOCAL_TIME
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TICKET_INFORMATION
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TRACK_1
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TRACK_2
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.TRANSMISSION_DATE_TIME
import com.kiwi.api.payments.adapter.jpos.models.FieldPosition.VARIOUS_INDICATORS
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.field.Aggregator
import com.kiwi.api.payments.domain.field.InputMode.CONTACTLESS
import com.kiwi.api.payments.domain.field.InputMode.ECOMMERCE
import com.kiwi.api.payments.domain.field.InputMode.EMV
import com.kiwi.api.payments.domain.field.InputMode.MANUAL
import com.kiwi.api.payments.domain.field.InputMode.STRIPE
import com.kiwi.api.payments.domain.field.Installments.Financing.AHORA
import com.kiwi.api.payments.domain.field.PreviousTransactionInputMode.FALLBACK
import com.kiwi.api.payments.extensions.toAcquirerFormat
import com.kiwi.api.payments.extensions.toDateInAcquirerFormat
import com.kiwi.api.payments.extensions.toTimeInAcquirerFormat
import org.springframework.stereotype.Component

@Component
class IsoFieldValueProvider {

    fun provide(position: FieldPosition, operation: Operation): String? =
        providers[position]?.provide(operation)

    companion object {
        private val providers: ValueProviders =
            mapOf(
                MTI to ValueProvider { it.mti.code },
                PAN to ValueProvider { if (it.inputMode in listOf(MANUAL, ECOMMERCE)) it.pan else null },
                PROCESS_CODE to ValueProvider {
                    with(it.processCode) {
                        transactionType.code + accountType.code + "00"
                    }
                },
                AMOUNT to ValueProvider { it.amount },
                TRANSMISSION_DATE_TIME to ValueProvider { it.terminalLocalDatetime.toAcquirerFormat() },
                AUDIT_NUMBER to ValueProvider { it.auditNumber },
                TERMINAL_LOCAL_TIME to ValueProvider { it.terminalLocalDatetime.toTimeInAcquirerFormat() },
                TERMINAL_LOCAL_DATE to ValueProvider { it.terminalLocalDatetime.toDateInAcquirerFormat() },
                EXPIRATION_DATE to ValueProvider { it.expirationDate },
                INPUT_MODE to ValueProvider { it.inputMode.toAcquirerFormat() },
                CARD_SEQUENCE_NUMBER to ValueProvider { it.cardSequenceNumber },
                NETWORK_INTERNATIONAL_IDENTIFIER to ValueProvider { it.networkInternationalIdentifier },
                POINT_OF_SERVICE_CONDITION_CODE to ValueProvider { it.pointOfServiceConditionCode },
                TRACK_2 to ValueProvider { it.track2?.replace("d|D".toRegex(), "=") },
                RETRIEVAL_REFERENCE_NUMBER to ValueProvider { it.retrievalReferenceNumber },
                TERMINAL_IDENTIFICATION to ValueProvider { it.terminalIdentification },
                COMMERCE_NUMBER to ValueProvider { it.commerceCode },
                TRACK_1 to ValueProvider { it.track1 },
                ADDITIONAL_DATA_ISO to ValueProvider {
                    it.track1Read?.let { isRead ->
                        if (isRead) " " else "1" + (it.previousTransactionInputMode?.code ?: "")
                    }
                },
                ADDITIONAL_DATA_PRIVATE to ValueProvider {
                    (if (it.installments.financing == AHORA) "7" else "0") + it.installments.quantity.padStart(2, '0')
                },
                CURRENCY to ValueProvider { it.currency.code },
                PIN_DATA to ValueProvider { it.pin },
                KSN to ValueProvider { it.ksn },
                ADDITIONAL_AMOUNTS to ValueProvider { it.additionalAmount },
                ICC_DATA to ValueProvider {
                    if (it.inputMode in listOf(CONTACTLESS, EMV)) it.iccData else it.cvv ?: it.iccData
                },
                VARIOUS_INDICATORS to getVariousIndicatorValueProvider(),
                APP_VERSION to ValueProvider
                {
                    it.appVersion.softwareVersion + it.appVersion.hardware + it.appVersion.handbookVersion
                },
                TICKET_INFORMATION to ValueProvider
                { it.ticket.padStart(4, '0') + it.batch.padStart(3, '0') },
            )
    }
}

typealias ValueProviders = Map<FieldPosition, ValueProvider>

fun interface ValueProvider {
    fun provide(operation: Operation): String?
}

private fun interface AggregatorValueProvider {
    fun provide(aggregator: Aggregator): String?
}

private fun getVariousIndicatorValueProvider() =
    ValueProvider { it ->
        ValueProvider { "0210001001070" + it.terminalFeature.code }.provide(it) +
            ValueProvider {
                if (it.previousTransactionInputMode == FALLBACK && it.inputMode == STRIPE) {
                    "0220001001071F"
                } else {
                    ""
                }
            }.provide(it) +
            ValueProvider {
                if (it.inputMode == ECOMMERCE) {
                    "02900010010791"
                } else {
                    ""
                }
            }.provide(it) +
            (
                it.aggregator?.let {
                    getAggregatorValueProvider().provide(it)
                } ?: ""
                ) +
            ValueProvider {
                if (it.previousTransactionInputMode == FALLBACK && it.inputMode == CONTACTLESS) {
                    "0810001001206F"
                } else {
                    ""
                }
            }.provide(it)
    }

private fun getAggregatorValueProvider() =
    AggregatorValueProvider {
        "0680008" +
            buildVariableLengthField(it.childCommerce.zip.take(8), "097") +
            buildVariableLengthField(it.commerceCode.take(3), "098") +
            buildVariableLengthField(
                it.name.take(7).padEnd(7, ' ')
                    .plus('*')
                    .plus(it.childCommerce.name.take(12)),
                "166"
            ) +
            buildVariableLengthField(it.address.take(20), "167") +
            buildVariableLengthField(
                it.childCommerce.categoryCode.take(5),
                "168"
            ) +
            buildVariableLengthField(it.childCommerce.code.take(15), "169") +
            buildVariableLengthField(it.childCommerce.city.take(13), "173") +
            buildVariableLengthField(it.childCommerce.state.code.take(5), "174")
    }

private fun String.getFieldLength() =
    count().toString().padStart(3, '0')

private fun buildVariableLengthField(field: String, position: String) =
    field.getFieldLength() + position + field
