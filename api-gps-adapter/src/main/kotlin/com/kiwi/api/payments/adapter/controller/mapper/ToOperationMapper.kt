package com.kiwi.api.payments.adapter.controller.mapper

import com.kiwi.api.payments.adapter.controller.models.OperationRequest
import com.kiwi.api.payments.adapter.controller.models.ReimbursementRequest
import com.kiwi.api.payments.domain.AcquirerCustomer
import com.kiwi.api.payments.domain.AcquirerMerchant
import com.kiwi.api.payments.domain.AcquirerTerminal
import com.kiwi.api.payments.domain.Customer
import com.kiwi.api.payments.domain.Merchant
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.PaymentType.OPERATION
import com.kiwi.api.payments.domain.field.Currency
import com.kiwi.api.payments.domain.field.InputMode
import com.kiwi.api.payments.domain.field.InputMode.STRIPE
import com.kiwi.api.payments.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.kiwi.api.payments.domain.field.PreviousTransactionInputMode
import com.kiwi.api.payments.domain.field.TerminalFeature
import com.kiwi.api.payments.domain.field.provider.AggregatorProvider
import com.kiwi.api.payments.domain.field.provider.AppVersionProvider
import com.kiwi.api.payments.domain.field.provider.ConstantsProvider
import com.kiwi.api.payments.domain.field.provider.InstallmentsProvider
import com.kiwi.api.payments.domain.field.provider.MtiProvider
import com.kiwi.api.payments.domain.field.provider.ProcessCodeProvider
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ToOperationMapper(
    private val processCodeProvider: ProcessCodeProvider,
    private val constantsProvider: ConstantsProvider,
    private val installmentsProvider: InstallmentsProvider,
    private val appVersionProvider: AppVersionProvider,
    private val mtiProvider: MtiProvider,
    private val aggregatorProvider: AggregatorProvider
) {
    // TODO: 022-fallback y 081-fallback contactless

    fun map(
        request: OperationRequest,
        operationType: OperationType,
        acquirerCustomer: AcquirerCustomer?,
        acquirerMerchant: AcquirerMerchant,
        acquirerTerminal: AcquirerTerminal
    ) =
        with(request) {
            Operation(
                mti = mtiProvider.provide(operationType),
                pan = capture.card.pan,
                processCode = processCodeProvider.provide(operationType),
                amount = amount.total,
                transmissionDatetime = datetime,
                auditNumber = trace,
                terminalLocalDatetime = datetime,
                expirationDate = capture.card.expirationDate,
                inputMode = InputMode.valueOf(capture.inputMode),
                previousTransactionInputMode = capture.previousTransactionInputMode?.let {
                    PreviousTransactionInputMode.valueOf(
                        it
                    )
                },
                cardSequenceNumber = capture.card.emv?.cardSequenceNumber,
                ksn = capture.card.emv?.ksn,
                iccData = capture.card.emv?.iccData,
                pin = capture.card.pin,
                networkInternationalIdentifier = constantsProvider.provideNII(),
                pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                track2 = capture.card.track2,
                retrievalReferenceNumber = retrievalReferenceNumber,
                terminalIdentification = acquirerTerminal.code,
                commerceCode = acquirerCustomer?.code ?: acquirerMerchant.code,
                track1 = capture.card.track1,
                track1Read = capture.track1Read(),
                installments = installmentsProvider.provideFrom(installments, operationType),
                currency = Currency.valueOf(amount.currency),
                additionalAmount = amount.getAdditionalAmount(),
                cvv = capture.card.cvv,
                appVersion = appVersionProvider.provide(terminal.softwareVersion),
                ticket = ticket,
                batch = batch,
                terminalFeature = terminal.features.getBestFeature(),
                aggregator = getAggregator(request.customer, request.merchant, acquirerCustomer, acquirerMerchant)
            )
        }

    fun map(
        request: ReimbursementRequest,
        operationType: OperationType,
        acquirerCustomer: AcquirerCustomer?,
        acquirerMerchant: AcquirerMerchant,
        acquirerTerminal: AcquirerTerminal
    ) =
        with(request) {
            Operation(
                mti = ONLINE_OPERATION_REQUEST,
                pan = capture.card.pan,
                processCode = processCodeProvider.provide(operationType),
                amount = amount.total,
                transmissionDatetime = OffsetDateTime.now(),
                auditNumber = trace,
                terminalLocalDatetime = datetime,
                expirationDate = capture.card.expirationDate,
                inputMode = InputMode.valueOf(capture.inputMode),
                previousTransactionInputMode = capture.previousTransactionInputMode?.let {
                    PreviousTransactionInputMode.valueOf(
                        it
                    )
                },
                cardSequenceNumber = capture.card.emv?.cardSequenceNumber,
                ksn = capture.card.emv?.ksn,
                iccData = capture.card.emv?.iccData,
                pin = capture.card.pin,
                networkInternationalIdentifier = constantsProvider.provideNII(),
                pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                track2 = capture.card.track2,
                retrievalReferenceNumber = retrievalReferenceNumber,
                terminalIdentification = acquirerTerminal.code,
                commerceCode = acquirerCustomer?.code ?: acquirerMerchant.code,
                track1 = capture.card.track1,
                track1Read = capture.track1Read(),
                installments = installmentsProvider.provideFrom(installments, operationType),
                currency = Currency.valueOf(amount.currency),
                additionalAmount = amount.getAdditionalAmount(),
                cvv = capture.card.cvv,
                appVersion = appVersionProvider.provide(terminal.softwareVersion),
                ticket = ticket,
                batch = batch,
                terminalFeature = terminal.features.getBestFeature(),
                aggregator = getAggregator(request.customer, request.merchant, acquirerCustomer, acquirerMerchant)
            )
        }

    private fun OperationRequest.Amount.getAdditionalAmount(): String? =
        breakdown
            .firstOrNull { it.description != OPERATION.name }
            ?.amount

    private fun OperationRequest.Capture.track1Read(): Boolean? =
        if (InputMode.valueOf(inputMode) == STRIPE) {
            !card.track1.isNullOrBlank()
        } else {
            null
        }

    private fun getAggregator(
        customer: Customer,
        merchant: Merchant,
        acquirerCustomer: AcquirerCustomer?,
        acquirerMerchant: AcquirerMerchant
    ) = acquirerCustomer?.let {
        aggregatorProvider.provide(customer, merchant, it, acquirerMerchant)
    }

    private fun ReimbursementRequest.Capture.track1Read(): Boolean? =
        if (InputMode.valueOf(inputMode) == STRIPE) {
            !card.track1.isNullOrBlank()
        } else {
            null
        }

    private fun ReimbursementRequest.Amount.getAdditionalAmount(): String? =
        breakdown
            .firstOrNull { it.description != OPERATION.name }
            ?.amount

    private fun List<String>.getBestFeature() =
        map { TerminalFeature.valueOf(it) }.maxByOrNull { it.code }!!
}
