package com.kiwi.api.payments.adapter.controller.mapper

import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_ADDRESS
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_CATEGORY_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_CITY
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_NAME
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_STATE
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CHILD_COMMERCE_ZIP
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_NAME
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_CVV
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_EXPIRATION_DATE
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_ICC_DATA
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_KSN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_PAN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_PIN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_SEQUENCE_NUMBER
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TRACK1
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TRACK2
import com.kiwi.api.payments.application.TestConstants.Companion.DATETIME
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_HANDBOOK_VERSION
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_HARDWARE
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_SOFTWARE_VERSION
import com.kiwi.api.payments.application.TestConstants.Companion.TIP_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TOTAL_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_BATCH
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_INSTALLMENTS
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_RRN
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TICKET
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TRACE
import com.kiwi.api.payments.application.aConstant
import com.kiwi.api.payments.application.anAcquirerCustomer
import com.kiwi.api.payments.application.anAcquirerMerchant
import com.kiwi.api.payments.application.anAcquirerTerminal
import com.kiwi.api.payments.application.anOperationRequest
import com.kiwi.api.payments.application.anReimbursementRequest
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.OperationType.ANNULMENT
import com.kiwi.api.payments.domain.OperationType.PURCHASE
import com.kiwi.api.payments.domain.field.Aggregator
import com.kiwi.api.payments.domain.field.Aggregator.ChildCommerce
import com.kiwi.api.payments.domain.field.AppVersion
import com.kiwi.api.payments.domain.field.Currency
import com.kiwi.api.payments.domain.field.InputMode.MANUAL
import com.kiwi.api.payments.domain.field.Installments
import com.kiwi.api.payments.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.kiwi.api.payments.domain.field.PreviousTransactionInputMode.CHIP
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.domain.field.ProcessCode.AccountType.DEFAULT
import com.kiwi.api.payments.domain.field.TerminalFeature
import com.kiwi.api.payments.domain.field.mapper.TransactionTypeMapper
import com.kiwi.api.payments.domain.field.provider.AggregatorProvider
import com.kiwi.api.payments.domain.field.provider.AppVersionProvider
import com.kiwi.api.payments.domain.field.provider.ConstantsProvider
import com.kiwi.api.payments.domain.field.provider.InstallmentsProvider
import com.kiwi.api.payments.domain.field.provider.MtiProvider
import com.kiwi.api.payments.domain.field.provider.ProcessCodeProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOperationMapperSpec : FeatureSpec({
    feature("map request") {

        beforeEach { clearAllMocks() }

        val constantsProvider = ConstantsProvider(aConstant())

        val mapper = ToOperationMapper(
            processCodeProvider = ProcessCodeProvider(TransactionTypeMapper()),
            constantsProvider = constantsProvider,
            installmentsProvider = InstallmentsProvider(),
            appVersionProvider = AppVersionProvider(constantsProvider),
            mtiProvider = MtiProvider(),
            aggregatorProvider = AggregatorProvider()
        )

        scenario("successful operation mapping for Aggregator Merchant") {
            val operationRequest = anOperationRequest()
            val acquirerCustomer = anAcquirerCustomer()
            val acquirerMerchant = anAcquirerMerchant()
            val acquirerTerminal = anAcquirerTerminal()
            val result = mapper.map(operationRequest, PURCHASE, acquirerCustomer, acquirerMerchant, acquirerTerminal)

            result shouldBe (
                Operation(
                    mti = ONLINE_OPERATION_REQUEST,
                    pan = CARD_PAN,
                    processCode = ProcessCode(
                        transactionType = ProcessCode.TransactionType.PURCHASE,
                        accountType = DEFAULT,
                        followMessage = false
                    ),
                    amount = TOTAL_AMOUNT,
                    transmissionDatetime = result.transmissionDatetime,
                    auditNumber = TRANSACTION_TRACE,
                    terminalLocalDatetime = DATETIME,
                    expirationDate = CARD_EXPIRATION_DATE,
                    inputMode = MANUAL,
                    previousTransactionInputMode = CHIP,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN,
                    iccData = CARD_ICC_DATA,
                    pin = CARD_PIN,
                    networkInternationalIdentifier = constantsProvider.provideNII(),
                    pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                    track2 = CARD_TRACK2,
                    retrievalReferenceNumber = TRANSACTION_RRN,
                    terminalIdentification = acquirerTerminal.code,
                    commerceCode = acquirerCustomer.code,
                    track1 = CARD_TRACK1,
                    track1Read = null,
                    installments = Installments(
                        financing = Installments.Financing.BANK,
                        quantity = TRANSACTION_INSTALLMENTS,
                    ),
                    currency = Currency.ARS,
                    additionalAmount = TIP_AMOUNT,
                    cvv = CARD_CVV,
                    appVersion = AppVersion(
                        hardware = TERMINAL_HARDWARE,
                        handbookVersion = TERMINAL_HANDBOOK_VERSION,
                        softwareVersion = TERMINAL_SOFTWARE_VERSION
                    ),
                    ticket = TRANSACTION_TICKET,
                    batch = TRANSACTION_BATCH,
                    terminalFeature = TerminalFeature.CONTACTLESS,
                    aggregator = Aggregator(
                        name = AGGREGATOR_NAME,
                        commerceCode = AGGREGATOR_CODE,
                        address = AGGREGATOR_ADDRESS,
                        childCommerce = ChildCommerce(
                            name = AGGREGATOR_CHILD_COMMERCE_NAME,
                            code = AGGREGATOR_CHILD_COMMERCE_CODE,
                            state = AGGREGATOR_CHILD_COMMERCE_STATE,
                            city = AGGREGATOR_CHILD_COMMERCE_CITY,
                            zip = AGGREGATOR_CHILD_COMMERCE_ZIP,
                            categoryCode = AGGREGATOR_CHILD_COMMERCE_CATEGORY_CODE
                        )
                    )
                )
                )
        }

        scenario("successful operation mapping for Aggregator Merchant") {
            val operationRequest = anOperationRequest()
            val acquirerCustomer = null
            val acquirerMerchant = anAcquirerMerchant()
            val acquirerTerminal = anAcquirerTerminal()
            val result = mapper.map(operationRequest, PURCHASE, acquirerCustomer, acquirerMerchant, acquirerTerminal)

            result shouldBe (
                Operation(
                    mti = ONLINE_OPERATION_REQUEST,
                    pan = CARD_PAN,
                    processCode = ProcessCode(
                        transactionType = ProcessCode.TransactionType.PURCHASE,
                        accountType = DEFAULT,
                        followMessage = false
                    ),
                    amount = TOTAL_AMOUNT,
                    transmissionDatetime = result.transmissionDatetime,
                    auditNumber = TRANSACTION_TRACE,
                    terminalLocalDatetime = DATETIME,
                    expirationDate = CARD_EXPIRATION_DATE,
                    inputMode = MANUAL,
                    previousTransactionInputMode = CHIP,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN,
                    iccData = CARD_ICC_DATA,
                    pin = CARD_PIN,
                    networkInternationalIdentifier = constantsProvider.provideNII(),
                    pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                    track2 = CARD_TRACK2,
                    retrievalReferenceNumber = TRANSACTION_RRN,
                    terminalIdentification = acquirerTerminal.code,
                    commerceCode = acquirerMerchant.code,
                    track1 = CARD_TRACK1,
                    track1Read = null,
                    installments = Installments(
                        financing = Installments.Financing.BANK,
                        quantity = TRANSACTION_INSTALLMENTS,
                    ),
                    currency = Currency.ARS,
                    additionalAmount = TIP_AMOUNT,
                    cvv = CARD_CVV,
                    appVersion = AppVersion(
                        hardware = TERMINAL_HARDWARE,
                        handbookVersion = TERMINAL_HANDBOOK_VERSION,
                        softwareVersion = TERMINAL_SOFTWARE_VERSION
                    ),
                    ticket = TRANSACTION_TICKET,
                    batch = TRANSACTION_BATCH,
                    terminalFeature = TerminalFeature.CONTACTLESS,
                    aggregator = null
                )
                )
        }

        scenario("successful reimbursement mapping Aggregator Merchant") {
            val reimbursementRequest = anReimbursementRequest()
            val acquirerCustomer = anAcquirerCustomer()
            val acquirerMerchant = anAcquirerMerchant()
            val acquirerTerminal = anAcquirerTerminal()
            val result =
                mapper.map(reimbursementRequest, ANNULMENT, acquirerCustomer, acquirerMerchant, acquirerTerminal)

            result shouldBe (
                Operation(
                    mti = ONLINE_OPERATION_REQUEST,
                    pan = CARD_PAN,
                    processCode = ProcessCode(
                        transactionType = ProcessCode.TransactionType.PURCHASE_ANNULMENT,
                        accountType = DEFAULT,
                        followMessage = false
                    ),
                    amount = TOTAL_AMOUNT,
                    transmissionDatetime = result.transmissionDatetime,
                    auditNumber = TRANSACTION_TRACE,
                    terminalLocalDatetime = DATETIME,
                    expirationDate = CARD_EXPIRATION_DATE,
                    inputMode = MANUAL,
                    previousTransactionInputMode = CHIP,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN,
                    iccData = CARD_ICC_DATA,
                    pin = CARD_PIN,
                    networkInternationalIdentifier = constantsProvider.provideNII(),
                    pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                    track2 = CARD_TRACK2,
                    retrievalReferenceNumber = TRANSACTION_RRN,
                    terminalIdentification = acquirerTerminal.code,
                    commerceCode = acquirerCustomer.code,
                    track1 = CARD_TRACK1,
                    track1Read = null,
                    installments = Installments(
                        financing = Installments.Financing.BANK,
                        quantity = TRANSACTION_INSTALLMENTS,
                    ),
                    currency = Currency.ARS,
                    additionalAmount = TIP_AMOUNT,
                    cvv = CARD_CVV,
                    appVersion = AppVersion(
                        hardware = TERMINAL_HARDWARE,
                        handbookVersion = TERMINAL_HANDBOOK_VERSION,
                        softwareVersion = TERMINAL_SOFTWARE_VERSION
                    ),
                    ticket = TRANSACTION_TICKET,
                    batch = TRANSACTION_BATCH,
                    terminalFeature = TerminalFeature.CONTACTLESS,
                    aggregator = Aggregator(
                        name = AGGREGATOR_NAME,
                        commerceCode = AGGREGATOR_CODE,
                        address = AGGREGATOR_ADDRESS,
                        childCommerce = ChildCommerce(
                            name = AGGREGATOR_CHILD_COMMERCE_NAME,
                            code = AGGREGATOR_CHILD_COMMERCE_CODE,
                            state = AGGREGATOR_CHILD_COMMERCE_STATE,
                            city = AGGREGATOR_CHILD_COMMERCE_CITY,
                            zip = AGGREGATOR_CHILD_COMMERCE_ZIP,
                            categoryCode = AGGREGATOR_CHILD_COMMERCE_CATEGORY_CODE
                        )
                    )
                )
                )
        }

        scenario("successful reimbursement mapping Direct Merchant") {
            val reimbursementRequest = anReimbursementRequest()
            val acquirerCustomer = null
            val acquirerMerchant = anAcquirerMerchant()
            val acquirerTerminal = anAcquirerTerminal()
            val result =
                mapper.map(reimbursementRequest, ANNULMENT, acquirerCustomer, acquirerMerchant, acquirerTerminal)

            result shouldBe (
                Operation(
                    mti = ONLINE_OPERATION_REQUEST,
                    pan = CARD_PAN,
                    processCode = ProcessCode(
                        transactionType = ProcessCode.TransactionType.PURCHASE_ANNULMENT,
                        accountType = DEFAULT,
                        followMessage = false
                    ),
                    amount = TOTAL_AMOUNT,
                    transmissionDatetime = result.transmissionDatetime,
                    auditNumber = TRANSACTION_TRACE,
                    terminalLocalDatetime = DATETIME,
                    expirationDate = CARD_EXPIRATION_DATE,
                    inputMode = MANUAL,
                    previousTransactionInputMode = CHIP,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN,
                    iccData = CARD_ICC_DATA,
                    pin = CARD_PIN,
                    networkInternationalIdentifier = constantsProvider.provideNII(),
                    pointOfServiceConditionCode = constantsProvider.providePOSConditionCode(),
                    track2 = CARD_TRACK2,
                    retrievalReferenceNumber = TRANSACTION_RRN,
                    terminalIdentification = acquirerTerminal.code,
                    commerceCode = acquirerMerchant.code,
                    track1 = CARD_TRACK1,
                    track1Read = null,
                    installments = Installments(
                        financing = Installments.Financing.BANK,
                        quantity = TRANSACTION_INSTALLMENTS,
                    ),
                    currency = Currency.ARS,
                    additionalAmount = TIP_AMOUNT,
                    cvv = CARD_CVV,
                    appVersion = AppVersion(
                        hardware = TERMINAL_HARDWARE,
                        handbookVersion = TERMINAL_HANDBOOK_VERSION,
                        softwareVersion = TERMINAL_SOFTWARE_VERSION
                    ),
                    ticket = TRANSACTION_TICKET,
                    batch = TRANSACTION_BATCH,
                    terminalFeature = TerminalFeature.CONTACTLESS,
                    aggregator = null
                )
                )
        }
    }
})
