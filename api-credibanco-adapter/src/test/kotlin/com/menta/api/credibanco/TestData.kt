package com.menta.api.credibanco

import com.menta.api.credibanco.TestConstants.Companion.AGGREGATOR_CODE
import com.menta.api.credibanco.TestConstants.Companion.AUDIT_NUMBER
import com.menta.api.credibanco.TestConstants.Companion.AUTHORIZATION_CODE
import com.menta.api.credibanco.TestConstants.Companion.CARD_PAN
import com.menta.api.credibanco.TestConstants.Companion.CARD_TYPE
import com.menta.api.credibanco.TestConstants.Companion.DATETIME
import com.menta.api.credibanco.TestConstants.Companion.OPERATION_RESPONSE_ID
import com.menta.api.credibanco.TestConstants.Companion.RECEIVING_INSTITUTION_IDENTIFICATION_CODE
import com.menta.api.credibanco.TestConstants.Companion.SETTLEMENT_DATA_RESPONSE
import com.menta.api.credibanco.TestConstants.Companion.TERMINAL_ID
import com.menta.api.credibanco.TestConstants.Companion.TOTAL_AMOUNT
import com.menta.api.credibanco.TestConstants.Companion.TRANSACTION_RRN
import com.menta.api.credibanco.TestConstants.Companion.WORKING_KEY
import com.menta.api.credibanco.adapter.controller.model.OperationRequest
import com.menta.api.credibanco.adapter.controller.model.OperationResponse
import com.menta.api.credibanco.adapter.db.entity.ResponseOperation
import com.menta.api.credibanco.adapter.jpos.models.ResponseCode
import com.menta.api.credibanco.config.Constants
import com.menta.api.credibanco.domain.Acquirer.CREDIBANCO
import com.menta.api.credibanco.domain.Address
import com.menta.api.credibanco.domain.Country.CO
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.CredibancoMerchant
import com.menta.api.credibanco.domain.CredibancoTerminal
import com.menta.api.credibanco.domain.Customer
import com.menta.api.credibanco.domain.Merchant
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.domain.Secret
import com.menta.api.credibanco.domain.SecretsTerminal
import com.menta.api.credibanco.domain.StatusCustomer
import com.menta.api.credibanco.domain.Terminal
import com.menta.api.credibanco.domain.field.CardType
import com.menta.api.credibanco.domain.field.CommerceData
import com.menta.api.credibanco.domain.field.Currency.COP
import com.menta.api.credibanco.domain.field.InputMode.STRIPE
import com.menta.api.credibanco.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.menta.api.credibanco.domain.field.MTI.ONLINE_OPERATION_RESPONSE
import com.menta.api.credibanco.domain.field.ProcessCode
import com.menta.api.credibanco.domain.field.SettlementData
import com.menta.api.credibanco.domain.field.TerminalData
import com.menta.api.credibanco.shared.error.ErrorHandler
import com.menta.api.credibanco.shared.error.model.ApiErrorResponse
import com.menta.api.credibanco.shared.error.providers.CurrentResourceProvider
import com.menta.api.credibanco.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.credibanco.shared.error.providers.ErrorResponseProvider
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.servlet.http.HttpServletRequest

val terminalId = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")
val merchantId = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")

fun aRequestPayment() =
    OperationRequest(
        capture = OperationRequest.Capture(
            card = OperationRequest.Capture.Card(
                holder = OperationRequest.Capture.Card.Holder(
                    name = "MTIP06  MCD  15A",
                    identification = OperationRequest.Capture.Card.Holder.Identification(
                        number = "0951377878",
                        type = "DNI"
                    )
                ),
                pan = CARD_PAN,
                expirationDate = "2512",
                cvv = "123",
                track1 = "5413330089020011D2512601079360805",
                track2 = "5413330089020011D2512601079360805",
                pin = "123",
                emv = OperationRequest.Capture.Card.EMV(
                    iccData = "CARD_ICC_DATA",
                    cardSequenceNumber = "123",
                    ksn = "123"
                ),
                bank = "SANTANDER",
                type = "CREDIT",
                brand = "MASTERCARD"
            ),
            inputMode = "STRIPE",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationRequest.Amount(
            total = TOTAL_AMOUNT,
            currency = "COP",
            breakdown = listOf(
                OperationRequest.Amount.Breakdown(description = "OPERATION", amount = TOTAL_AMOUNT),
            )
        ),
        datetime = aDate(),
        trace = "000002",
        ticket = "2",
        terminal = Terminal(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            merchantId = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            customerId = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            serialCode = "03000021",
            hardwareVersion = "abc123",
            softwareVersion = "10",
            tradeMark = "pirutchit",
            model = "zg300",
            status = "ACTIVE",
            features = listOf(
                "CONTACTLESS"
            )
        ),
        installments = "01",
        merchant = Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            customerId = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = CO,
            legalType = "NATURAL_PERSON",
            businessName = "Gerbers gin",
            fantasyName = "Menta",
            businessOwner = Merchant.BusinessOwner(
                name = "Jose",
                surname = "Perez",
                birthDate = "2022-01-19T11:23:23Z",
                id = Merchant.BusinessOwner.OwnerId(
                    type = "DNI",
                    number = "999999999"
                )
            ),
            merchantCode = "123",
            address = Address(
                state = "Antioquia",
                city = "Medellin",
                zip = "5001",
                street = "Street1",
                number = "123",
                floor = "1",
                apartment = "A"
            ),
            email = "hola@hola",
            phone = "1111111111",
            activity = "a activity",
            category = "7372",
            tax = Merchant.Tax(
                id = "id",
                type = "monotributista"
            ),
            settlementCondition = Merchant.SettlementCondition(
                transactionFee = "transactionFee",
                settlement = "settlement",
                cbuOrCvu = "123123123123123"
            ),
            representative = Merchant.Representative(
                id = Merchant.Representative.RepresentativeId(
                    type = "type",
                    number = "number"
                ),
                birthDate = "2022-01-19T11:23:23Z",
                name = "representativeName",
                surname = "representativeSurname"
            )
        ),
        batch = "2",
        retrievalReferenceNumber = null,
        customer = Customer(
            id = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = CO,
            businessName = "name43543",
            fantasyName = "fantasyName1",
            activity = "Activity",
            status = StatusCustomer.ACTIVE,
            tax = Customer.Tax(
                type = "1",
                id = "2"
            ),
            address = Address(
                state = "Antioquia",
                city = "Medellin",
                zip = "5001",
                street = "Street1",
                number = "125",
                floor = "1",
                apartment = "A"
            ),
            category = "Category"
        )
    )

val anOperation =
    Operation(
        mti = ONLINE_OPERATION_REQUEST,
        pan = CARD_PAN,
        processCode = ProcessCode.CREDIT_PURCHASE,
        amount = TOTAL_AMOUNT,
        transmissionDatetime = aDate().plusHours(5),
        auditNumber = "000002",
        terminalLocalDatetime = aDate(),
        terminalCaptureDate = aDate(),
        merchantType = "5812",
        inputMode = STRIPE,
        pointOfServiceConditionCode = "00",
        acquiringInstitutionIdentificationCode = "           ",
        track2 = "5413330089020011D2512601079360805",
        retrievalReferenceNumber = null,
        authorizationIdentificationResponse = null,
        terminalIdentification = "000DG808",
        installments = "01",
        commerceCode = "010029213",
        commerceData = CommerceData(
            name = "Gerbers gin",
            terminalCity = CommerceData.TerminalCity(
                code = "5001",
                name = "Medellin"
            ),
            state = "Antioquia",
            country = CO
        ),
        additionalDataNational = null,
        additionalDataPrivate = "010002319",
        currency = COP,
        pin = "123",
        additionalAmount = TOTAL_AMOUNT,
        terminalData = TerminalData(
            ownerFiid = "0090",
            logicalNetwork = "CER2",
            timeOffset = "+000",
            terminalId = "0000"
        ),
        cardIssuerCategory = "    CER200000000000",
        additionalInformation = null,
        receivingInstitutionIdenficationCode = "           ",
        infoText = "         ",
        networkManagementInformation = null,
        messageAuthenticationCode = null,
        iccData = "CARD_ICC_DATA",
        settlementData = "  B24 B24 1 "
    )

fun aCreatedOperation() =
    CreatedOperation(
        mti = ONLINE_OPERATION_RESPONSE,
        processCode = ProcessCode.CREDIT_PURCHASE,
        amount = TOTAL_AMOUNT,
        transmissionDatetime = DATETIME,
        auditNumber = AUDIT_NUMBER,
        terminalDatetime = DATETIME,
        retrievalReferenceNumber = TRANSACTION_RRN,
        authorizationCode = AUTHORIZATION_CODE,
        responseCode = ResponseCode.APPROVED,
        terminalIdentification = TERMINAL_ID,
        commerceNumber = AGGREGATOR_CODE,
        cardNationality = null,
        currency = COP,
        workingKey = WORKING_KEY,
        cardType = CARD_TYPE,
        displayMessage = null,
        receivingInstitutionIdentificationCode = RECEIVING_INSTITUTION_IDENTIFICATION_CODE,
        settlementDataResponse = SETTLEMENT_DATA_RESPONSE
    )

fun aPaymentResponse() =
    OperationResponse(
        capture = OperationResponse.Capture(
            card = OperationResponse.Capture.Card(
                holder = OperationResponse.Capture.Card.Holder(
                    name = "MTIP06  MCD  15A",
                    identification = OperationResponse.Capture.Card.Holder.Identification(
                        number = "0951377878",
                        type = "DNI"
                    )
                ),
                iccData = "CARD_ICC_DATA",
                maskedPan = "XXXXXXXXXXXX0011",
                workingKey = "123456",
                bank = "SANTANDER",
                type = "CREDIT",
                brand = "MASTERCARD",
                nationality = null
            ),
            inputMode = "STRIPE",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationResponse.Amount(
            total = "200000",
            currency = "COP",
            breakdown = listOf(
                OperationResponse.Amount.Breakdown(
                    description = "OPERATION",
                    amount = "200000"
                )
            )
        ),
        datetime = aDate(),
        trace = "000002",
        ticket = "2",
        terminal = OperationResponse.Terminal(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            serialCode = "03000021",
            softwareVersion = "10",
            features = listOf(
                "CONTACTLESS"
            )
        ),
        batch = "2",
        installments = "01",
        authorization = OperationResponse.Authorization(
            code = "00",
            retrievalReferenceNumber = "123456789012",
            status = OperationResponse.Status(
                code = "APPROVED",
                situation = OperationResponse.Status.Situation(
                    id = "00",
                    description = "APPROVED"
                )
            )
        ),
        displayMessage = null,
        merchant = OperationResponse.Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
        )
    )

fun aCredibancoTerminal() =
    CredibancoTerminal(
        terminalId = terminalId,
        logicId = "000DG808",
        uniqueCode = "010002319"
    )

fun aCredibancoMerchant() =
    CredibancoMerchant(
        merchantId = merchantId,
        commerceCode = "010029213",
        category = "5812"
    )

fun aConstant() = Constants(
    networkInternationalIdentifier = "012",
    pointOfServiceConditionCode = "00",
    terminalOwnerFiid = "0090",
    terminalLogicalNetwork = "CER2",
    terminalTimeOffset = "+000",
    terminalId = "0000",
    closeTransactionHour = 23,
    infoText = "         ",
    acquiringInstitutionIdentificationCode = "           "
)

fun aSettlementData() = SettlementData(
    service = "  ",
    originator = "B24 ",
    destination = "B24 ",
    draftCaptureFlag = "1",
    settlementFlag = " "
)

fun aTerminalType() = CardType(
    cardIssuerFiid = "    ",
    logicalNetwork = "CER2",
    category = "0",
    saveAccountIndicator = "00",
    interchangeResponseCode = "00000000"
)

fun aResponseOperation() =
    ResponseOperation(
        id = UUID.fromString(OPERATION_RESPONSE_ID),
        retrievalReferenceNumber = TRANSACTION_RRN,
        authorizationCode = AUTHORIZATION_CODE,
        responseCode = ResponseCode.APPROVED.code,
        cardTypeResponseCode = CARD_TYPE,
        receivingInstitutionIdenficationCode = RECEIVING_INSTITUTION_IDENTIFICATION_CODE,
        settlementData = SETTLEMENT_DATA_RESPONSE
    )

fun aDate() = OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)

val anAcquirer = CREDIBANCO

const val aTerminalId = "0ce8dc16-da32-4dbb-9acc-c29ed52c6aca"
const val aCustomerId = "e56147b9-dcab-4a1a-a2b5-3f56f083d5a6"
const val aMerchantId = "a9b697f3-2bd0-43f7-b403-7885058d8ce7"

val aSecret =
    Secret(
        acquirer = anAcquirer,
        algorithm = "3DES",
        params = mapOf(
            "key" to "value"
        )
    )

val aTerminal =
    Terminal(
        id = UUID.fromString(aTerminalId),
        customerId = UUID.fromString(aCustomerId),
        merchantId = UUID.fromString(aMerchantId),
        serialCode = "321654987",
        hardwareVersion = "abc123",
        softwareVersion = "kkt-2536",
        tradeMark = "UROVO",
        model = "zg300",
        status = "ACTIVE",
        features = listOf("MANUAL", "STRIPE", "CHIP")
    )

val aSecretsTerminal =
    SecretsTerminal(
        serialCode = aTerminal.serialCode,
        id = UUID.fromString(aTerminalId),
        merchant = SecretsTerminal.Merchant(
            id = UUID.fromString(aMerchantId)
        ),
        customer = SecretsTerminal.Customer(
            id = UUID.fromString(aCustomerId)
        )
    )

val anApiErrorResponse =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiErrorResponse.ApiError(
                code = "210",
                resource = "a resource",
                message = "terminal ${aRequestPayment().terminal.serialCode} configuration outdated",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )

fun aControllerAdvice(request: HttpServletRequest) = ErrorHandler(
    errorResponseProvider = ErrorResponseProvider(
        currentResourceProvider = CurrentResourceProvider(request),
        metadataProvider = ErrorResponseMetadataProvider(
            currentResourceProvider = CurrentResourceProvider(request)
        )
    )
)
