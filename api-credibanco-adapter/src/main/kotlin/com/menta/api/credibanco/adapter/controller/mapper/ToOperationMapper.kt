package com.menta.api.credibanco.adapter.controller.mapper

import com.menta.api.credibanco.adapter.controller.model.OperationRequest
import com.menta.api.credibanco.adapter.db.entity.ResponseOperation
import com.menta.api.credibanco.domain.CredibancoMerchant
import com.menta.api.credibanco.domain.CredibancoTerminal
import com.menta.api.credibanco.domain.Merchant
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.OperationType.ANNULMENT
import com.menta.api.credibanco.domain.OperationType.ANNULMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PAYMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.domain.OperationType.REFUND
import com.menta.api.credibanco.domain.field.CommerceData
import com.menta.api.credibanco.domain.field.Currency
import com.menta.api.credibanco.domain.field.InputMode
import com.menta.api.credibanco.domain.field.provider.CardTypeProvider
import com.menta.api.credibanco.domain.field.provider.ConstantsProvider
import com.menta.api.credibanco.domain.field.provider.MtiProvider
import com.menta.api.credibanco.domain.field.provider.ProcessCodeProvider
import com.menta.api.credibanco.domain.field.provider.SettlementDataProvider
import com.menta.api.credibanco.domain.field.provider.TerminalDataProvider
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ToOperationMapper(
    private val mtiProvider: MtiProvider,
    private val processCodeProvider: ProcessCodeProvider,
    private val constantsProvider: ConstantsProvider,
    private val terminalDataProvider: TerminalDataProvider,
    private val cardTypeProvider: CardTypeProvider,
    private val settlementDataProvider: SettlementDataProvider
) {

    fun map(
        request: OperationRequest,
        operationType: OperationType,
        credibancoTerminal: CredibancoTerminal,
        credibancoMerchant: CredibancoMerchant,
        responseOperation: ResponseOperation? = null
    ) =
        with(request) {
            Operation(
                mti = mtiProvider.provide(operationType), // VA EN EL HEADER
                pan = capture.card.pan,
                processCode = processCodeProvider.provider(capture.card.type, operationType),
                amount = amount.total,
                transmissionDatetime = datetime.inGMTPlus5(),
                auditNumber = trace,
                terminalLocalDatetime = datetime,
                terminalCaptureDate = datetime.getCaptureDate(),
                merchantType = credibancoMerchant.category, // categoria del merchant debe coincidir con credibanco
                inputMode = InputMode.valueOf(capture.inputMode),
                pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                acquiringInstitutionIdentificationCode = constantsProvider.getAcquiringInstitutionIdentificationCode(), // M pedir el value a bancolombia
                track2 = capture.card.track2,
                retrievalReferenceNumber = retrievalReferenceNumber,
                authorizationIdentificationResponse = getAuthorizationIdentificationResponse(operationType, responseOperation?.authorizationCode),
                terminalIdentification = credibancoTerminal.logicId, // "acquiererTerminal.code", (si el codigo tiene menos de 8 digitios padear con 0)
                commerceCode = credibancoMerchant.commerceCode, // "acquiererCustomer.commerceCode",
                installments = installments,
                commerceData = getCommerceData(request.merchant),
                additionalDataNational = null,
                additionalDataPrivate = credibancoTerminal.uniqueCode, // este codigo unico no los pasa Credibanco
                currency = Currency.valueOf(amount.currency),
                pin = capture.card.pin,
                additionalAmount = amount.total,
                terminalData = terminalDataProvider.provide(),
                cardIssuerCategory = cardTypeProvider.provide(operationType, responseOperation?.cardTypeResponseCode), // para 420 mapear la respuesta 210
                additionalInformation = null, // nose
                receivingInstitutionIdenficationCode = getReceivingInstitutionIdenficationCode(operationType, responseOperation?.receivingInstitutionIdenficationCode), // para 420 mapear la respuesta 210
                infoText = constantsProvider.provideInfoText(),
                networkManagementInformation = null,
                messageAuthenticationCode = null,
                iccData = capture.card.emv?.iccData,
                settlementData = settlementDataProvider.provide(operationType, responseOperation?.settlementData)
            )
        }

    private fun OffsetDateTime.inGMTPlus5(): OffsetDateTime =
        this.plusHours(5)

    private fun OffsetDateTime.getCaptureDate(): OffsetDateTime {
        if (this.hour > constantsProvider.provideCloseTransactionHour()) {
            return this.plusDays(1)
        }
        return this
    }

    private fun getCommerceData(merchant: Merchant) =
        with(merchant) {
            CommerceData(
                name = businessName,
                terminalCity = CommerceData.TerminalCity(
                    code = address.zip,
                    name = address.city
                ),
                state = address.state,
                country = country
            )
        }

    private fun getReceivingInstitutionIdenficationCode(operationType: OperationType, codeResponse: String?) =
        when (operationType) {
            PURCHASE, ANNULMENT -> RECEIVING_INSTITUTION_IDENTIFICATION_CODE
            PAYMENT_REVERSE, REFUND -> codeResponse
            ANNULMENT_REVERSE -> null
        }

    private fun getAuthorizationIdentificationResponse(operationType: OperationType, authIdentification: String?) =
        when (operationType) {
            ANNULMENT -> authIdentification
            else -> { null }
        }

    companion object {
        const val RECEIVING_INSTITUTION_IDENTIFICATION_CODE = "           "
    }
}
