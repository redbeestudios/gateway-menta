package com.kiwi.api.payments.application

import com.kiwi.api.payments.adapter.controller.models.OperationRequest
import com.kiwi.api.payments.adapter.controller.models.OperationResponse
import com.kiwi.api.payments.adapter.controller.models.ReimbursementRequest
import com.kiwi.api.payments.adapter.controller.models.ReimbursementResponse
import com.kiwi.api.payments.adapter.jpos.models.ResponseCode.APPROVED
import com.kiwi.api.payments.application.TestConstants.Companion.AGGREGATOR_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.AUDIT_NUMBER
import com.kiwi.api.payments.application.TestConstants.Companion.AUTHORIZATION_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_BANK
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_BRAND
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_CVV
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_EXPIRATION_DATE
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_ICC_DATA
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_KSN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_NII
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_PAN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_PIN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_SEQUENCE_NUMBER
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TRACK1
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TRACK2
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TYPE
import com.kiwi.api.payments.application.TestConstants.Companion.DATETIME
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICACION_NAME
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICATION_NUMBER
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICATION_TYPE
import com.kiwi.api.payments.application.TestConstants.Companion.OPERATION_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_ID
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_MESSAGE
import com.kiwi.api.payments.application.TestConstants.Companion.TIP_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TOTAL_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_BATCH
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_INSTALLMENTS
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_RRN
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TICKET
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TRACE
import com.kiwi.api.payments.application.TestConstants.Companion.WORKING_KEY
import com.kiwi.api.payments.config.Constants
import com.kiwi.api.payments.domain.AcquirerCustomer
import com.kiwi.api.payments.domain.AcquirerMerchant
import com.kiwi.api.payments.domain.AcquirerTerminal
import com.kiwi.api.payments.domain.Address
import com.kiwi.api.payments.domain.Country.ARG
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.CreatedOperation.DisplayMessage
import com.kiwi.api.payments.domain.CreatedOperation.DisplayMessage.UseCode.DISPLAY_TEXT
import com.kiwi.api.payments.domain.Customer
import com.kiwi.api.payments.domain.Merchant
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.State.CORDOBA
import com.kiwi.api.payments.domain.StatusCustomer
import com.kiwi.api.payments.domain.Terminal
import com.kiwi.api.payments.domain.field.Aggregator
import com.kiwi.api.payments.domain.field.AppVersion
import com.kiwi.api.payments.domain.field.Currency.ARS
import com.kiwi.api.payments.domain.field.InputMode.MANUAL
import com.kiwi.api.payments.domain.field.Installments
import com.kiwi.api.payments.domain.field.Installments.Financing.BANK
import com.kiwi.api.payments.domain.field.MTI.ONLINE_OPERATION_REQUEST
import com.kiwi.api.payments.domain.field.MTI.ONLINE_OPERATION_RESPONSE
import com.kiwi.api.payments.domain.field.PreviousTransactionInputMode.CHIP
import com.kiwi.api.payments.domain.field.ProcessCode
import com.kiwi.api.payments.domain.field.ProcessCode.AccountType.DEFAULT
import com.kiwi.api.payments.domain.field.ProcessCode.TransactionType.PURCHASE
import com.kiwi.api.payments.domain.field.TerminalFeature.CONTACTLESS
import com.kiwi.api.payments.shared.error.model.ApiErrorResponse
import com.kiwi.api.payments.shared.error.model.ApiErrorResponse.ApiError
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

fun anOperationRequest() =
    OperationRequest(
        capture = OperationRequest.Capture(
            card = OperationRequest.Capture.Card(
                holder = OperationRequest.Capture.Card.Holder(
                    name = IDENTIFICACION_NAME,
                    identification = OperationRequest.Capture.Card.Holder.Identification(
                        number = IDENTIFICATION_NUMBER,
                        type = IDENTIFICATION_TYPE
                    )
                ),
                pan = CARD_PAN,
                expirationDate = CARD_EXPIRATION_DATE,
                cvv = CARD_CVV,
                track1 = CARD_TRACK1,
                track2 = CARD_TRACK2,
                pin = CARD_PIN,
                emv = OperationRequest.Capture.Card.EMV(
                    iccData = CARD_ICC_DATA,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN
                ),
                bank = CARD_BANK,
                type = CARD_TYPE,
                brand = CARD_BRAND
            ),
            inputMode = "MANUAL",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationRequest.Amount(
            total = TOTAL_AMOUNT,
            currency = "ARS",
            breakdown = listOf(
                OperationRequest.Amount.Breakdown(description = "OPERATION", amount = OPERATION_AMOUNT),
                OperationRequest.Amount.Breakdown(description = "TIP", amount = TIP_AMOUNT)
            )
        ),
        datetime = DATETIME,
        trace = TRANSACTION_TRACE,
        ticket = TRANSACTION_TICKET,
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
        installments = TRANSACTION_INSTALLMENTS,
        merchant = Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            customerId = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            legalType = "NATURAL_PERSON",
            businessName = "Kiwi",
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
                state = "CORDOBA",
                city = "CABA",
                zip = "123",
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
        batch = TRANSACTION_BATCH,
        retrievalReferenceNumber = TRANSACTION_RRN,
        customer = Customer(
            id = UUID.fromString("8f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            businessName = "name43543",
            fantasyName = "fantasyName1",
            activity = "Activity",
            status = StatusCustomer.ACTIVE,
            tax = Customer.Tax(
                type = "1",
                id = "2"
            ),
            address = Address(
                state = "Argentina",
                city = "CABA",
                zip = "123",
                street = "Street1",
                number = "123",
                floor = "1",
                apartment = "A"
            )
        )
    )

fun anRequestPayment() =
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
                pan = "5413330089020011",
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
            inputMode = "EMV",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationRequest.Amount(
            total = "2000",
            currency = "ARS",
            breakdown = listOf(
                OperationRequest.Amount.Breakdown(description = "OPERATION", amount = "2000"),
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
            country = ARG,
            legalType = "NATURAL_PERSON",
            businessName = "Kiwi",
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
                state = "Argentina",
                city = "CABA",
                zip = "123",
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
        retrievalReferenceNumber = "111111111111",
        customer = Customer(
            id = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            businessName = "name43543",
            fantasyName = "fantasyName1",
            activity = "Activity",
            status = StatusCustomer.ACTIVE,
            tax = Customer.Tax(
                type = "1",
                id = "2"
            ),
            address = Address(
                state = "Argentina",
                city = "CABA",
                zip = "123",
                street = "Street1",
                number = "123",
                floor = "1",
                apartment = "A"
            )
        )
    )

fun anRequestReimbursement() =
    ReimbursementRequest(
        capture = ReimbursementRequest.Capture(
            card = ReimbursementRequest.Capture.Card(
                holder = ReimbursementRequest.Capture.Card.Holder(
                    name = "MTIP06  MCD  15A",
                    identification = ReimbursementRequest.Capture.Card.Holder.Identification(
                        number = "0951377878",
                        type = "DNI"
                    )
                ),
                pan = "5413330089020011",
                expirationDate = "2512",
                cvv = "123",
                track1 = "5413330089020011D2512601079360805",
                track2 = "5413330089020011D2512601079360805",
                pin = "123",
                emv = ReimbursementRequest.Capture.Card.EMV(
                    iccData = "CARD_ICC_DATA",
                    cardSequenceNumber = "123",
                    ksn = "123"
                ),
                bank = "SANTANDER",
                type = "CREDIT",
                brand = "MASTERCARD"
            ),
            inputMode = "EMV",
            previousTransactionInputMode = "CHIP"
        ),
        amount = ReimbursementRequest.Amount(
            total = "2000",
            currency = "ARS",
            breakdown = listOf(
                ReimbursementRequest.Amount.Breakdown(description = "OPERATION", amount = "2000"),
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
            country = ARG,
            legalType = "NATURAL_PERSON",
            businessName = "Kiwi",
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
                state = "Argentina",
                city = "CABA",
                zip = "123",
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
        retrievalReferenceNumber = "111111111111",
        customer = Customer(
            id = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            businessName = "name43543",
            fantasyName = "fantasyName1",
            activity = "Activity",
            status = StatusCustomer.ACTIVE,
            tax = Customer.Tax(
                type = "1",
                id = "2"
            ),
            address = Address(
                state = "Argentina",
                city = "CABA",
                zip = "123",
                street = "Street1",
                number = "123",
                floor = "1",
                apartment = "A"
            )
        )
    )

fun anReimbursementRequest() =
    ReimbursementRequest(
        capture = ReimbursementRequest.Capture(
            card = ReimbursementRequest.Capture.Card(
                holder = ReimbursementRequest.Capture.Card.Holder(
                    name = IDENTIFICACION_NAME,
                    identification = ReimbursementRequest.Capture.Card.Holder.Identification(
                        number = IDENTIFICATION_NUMBER,
                        type = IDENTIFICATION_TYPE
                    )
                ),
                pan = CARD_PAN,
                expirationDate = CARD_EXPIRATION_DATE,
                cvv = CARD_CVV,
                track1 = CARD_TRACK1,
                track2 = CARD_TRACK2,
                pin = CARD_PIN,
                emv = ReimbursementRequest.Capture.Card.EMV(
                    iccData = CARD_ICC_DATA,
                    cardSequenceNumber = CARD_SEQUENCE_NUMBER,
                    ksn = CARD_KSN
                ),
                bank = CARD_BANK,
                type = CARD_TYPE,
                brand = CARD_BRAND
            ),
            inputMode = "MANUAL",
            previousTransactionInputMode = "CHIP"
        ),
        amount = ReimbursementRequest.Amount(
            total = TOTAL_AMOUNT,
            currency = "ARS",
            breakdown = listOf(
                ReimbursementRequest.Amount.Breakdown(
                    description = "OPERATION",
                    amount = OPERATION_AMOUNT
                ),
                ReimbursementRequest.Amount.Breakdown(description = "TIP", amount = TIP_AMOUNT)
            )
        ),
        datetime = DATETIME,
        trace = TRANSACTION_TRACE,
        ticket = TRANSACTION_TICKET,
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
        installments = TRANSACTION_INSTALLMENTS,
        merchant = Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            customerId = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            legalType = "NATURAL_PERSON",
            businessName = "Kiwi",
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
                state = "CORDOBA",
                city = "CABA",
                zip = "123",
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
        batch = TRANSACTION_BATCH,
        retrievalReferenceNumber = TRANSACTION_RRN,
        customer = Customer(
            id = UUID.fromString("6f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
            businessName = "name43543",
            fantasyName = "fantasyName1",
            activity = "Activity",
            status = StatusCustomer.ACTIVE,
            tax = Customer.Tax(
                type = "1",
                id = "2"
            ),
            address = Address(
                state = "Argentina",
                city = "CABA",
                zip = "123",
                street = "Street1",
                number = "123",
                floor = "1",
                apartment = "A"
            )
        )
    )

fun aCreatedOperation() =
    CreatedOperation(
        mti = ONLINE_OPERATION_RESPONSE,
        pan = CARD_PAN,
        processCode = ProcessCode(
            transactionType = PURCHASE,
            accountType = DEFAULT,
            followMessage = false
        ),
        amount = TOTAL_AMOUNT,
        transmissionDatetime = DATETIME,
        auditNumber = AUDIT_NUMBER,
        terminalDatetime = DATETIME,
        networkInternationalIdentifier = CARD_NII,
        retrievalReferenceNumber = TRANSACTION_RRN,
        authorizationCode = AUTHORIZATION_CODE,
        responseCode = APPROVED,
        terminalIdentification = TERMINAL_ID,
        commerceNumber = AGGREGATOR_CODE,
        cardNationality = null,
        currency = ARS,
        workingKey = WORKING_KEY,
        displayMessage = DisplayMessage(
            useCode = DISPLAY_TEXT,
            message = TERMINAL_MESSAGE
        ),
        iccData = CARD_ICC_DATA
    )

val anOperation =
    Operation(
        mti = ONLINE_OPERATION_REQUEST,
        pan = CARD_PAN,
        processCode = ProcessCode(
            transactionType = PURCHASE,
            accountType = DEFAULT,
            followMessage = false
        ),
        amount = TOTAL_AMOUNT,
        transmissionDatetime = aDate(),
        auditNumber = "123456789",
        terminalLocalDatetime = aDate(),
        expirationDate = CARD_EXPIRATION_DATE,
        inputMode = MANUAL,
        previousTransactionInputMode = CHIP,
        cardSequenceNumber = CARD_SEQUENCE_NUMBER,
        ksn = "123",
        iccData = "1234",
        pin = "1234",
        networkInternationalIdentifier = "234567",
        pointOfServiceConditionCode = "3456",
        track2 = "0843793270783jkj38",
        retrievalReferenceNumber = "9876543",
        terminalIdentification = "1",
        commerceCode = "666",
        track1 = "1234%67990",
        track1Read = true,
        installments = Installments(
            financing = BANK,
            quantity = "10",
        ),
        currency = ARS,
        additionalAmount = "1000",
        cvv = "755",
        appVersion = AppVersion(
            hardware = "duro",
            handbookVersion = "200",
            softwareVersion = "1.0.456"
        ),
        ticket = "234",
        batch = "12234566",
        terminalFeature = CONTACTLESS,
        aggregator = Aggregator(
            name = "MENTA  *MENTA SUBCOMER",
            commerceCode = "12",
            address = "Miguel Calixto D 312",
            childCommerce = Aggregator.ChildCommerce(
                name = "MENTA SUBCOMER",
                code = "666",
                state = CORDOBA,
                city = "CORDOBA CAPIT",
                zip = "5000",
                categoryCode = "7372"
            )
        )
    )

val aDirectMerchantOperation =
    Operation(
        mti = ONLINE_OPERATION_REQUEST,
        pan = CARD_PAN,
        processCode = ProcessCode(
            transactionType = PURCHASE,
            accountType = DEFAULT,
            followMessage = false
        ),
        amount = TOTAL_AMOUNT,
        transmissionDatetime = aDate(),
        auditNumber = "123456789",
        terminalLocalDatetime = aDate(),
        expirationDate = CARD_EXPIRATION_DATE,
        inputMode = MANUAL,
        previousTransactionInputMode = CHIP,
        cardSequenceNumber = CARD_SEQUENCE_NUMBER,
        ksn = "123",
        iccData = "1234",
        pin = "1234",
        networkInternationalIdentifier = "234567",
        pointOfServiceConditionCode = "3456",
        track2 = "0843793270783jkj38",
        retrievalReferenceNumber = "9876543",
        terminalIdentification = "1",
        commerceCode = "666",
        track1 = "1234%67990",
        track1Read = true,
        installments = Installments(
            financing = BANK,
            quantity = "10",
        ),
        currency = ARS,
        additionalAmount = "1000",
        cvv = "755",
        appVersion = AppVersion(
            hardware = "duro",
            handbookVersion = "200",
            softwareVersion = "1.0.456"
        ),
        ticket = "234",
        batch = "12234566",
        terminalFeature = CONTACTLESS,
        aggregator = null
    )

fun aPaymentResponse() =
    OperationResponse(
        capture = OperationResponse.Capture(
            card = OperationResponse.Capture.Card(
                holder = OperationResponse.Capture.Card.Holder(
                    name = "sebastian",
                    identification = OperationResponse.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                iccData = "1234",
                maskedPan = "XXXXXXXXXXXX1245",
                workingKey = "1223",
                bank = "bank",
                type = "type",
                brand = "brand",
                nationality = "ARGENTINA"
            ),
            inputMode = "45466",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                OperationResponse.Amount.Breakdown(
                    description = "OPERATION",
                    amount = "1000"
                )
            )
        ),
        datetime = aDate(),
        trace = "123",
        ticket = "234",
        terminal = OperationResponse.Terminal(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            serialCode = "1",
            softwareVersion = "1.0.456",
            features = listOf("ONE", "TWO", "THREE")
        ),
        batch = "12234566",
        installments = "10",
        authorization = OperationResponse.Authorization(
            code = "123-12",
            retrievalReferenceNumber = "9876543",
            status = OperationResponse.Status(
                code = "APPROVED",
                situation = OperationResponse.Status.Situation(
                    id = "302",
                    description = "This is a description"
                )
            )
        ),
        displayMessage = OperationResponse.DisplayMessage(
            useCode = "22",
            message = "Necesita Impresion"
        ),
        merchant = OperationResponse.Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
        )
    )

fun aReimbursementResponse() =
    ReimbursementResponse(
        capture = ReimbursementResponse.Capture(
            card = ReimbursementResponse.Capture.Card(
                holder = ReimbursementResponse.Capture.Card.Holder(
                    name = "sebastian",
                    identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                iccData = "1234",
                maskedPan = "XXXXXXXXXXXX1245",
                workingKey = "1223",
                bank = "bank",
                type = "type",
                brand = "brand",
                nationality = "ARGENTINA"
            ),
            inputMode = "45466",
            previousTransactionInputMode = "CHIP"
        ),
        amount = ReimbursementResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                ReimbursementResponse.Amount.Breakdown(
                    description = "OPERATION",
                    amount = "1000"
                )
            )
        ),
        datetime = aDate(),
        trace = "123",
        ticket = "234",
        terminal = ReimbursementResponse.Terminal(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
            serialCode = "1",
            softwareVersion = "1.0.456",
            features = listOf("ONE", "TWO", "THREE")
        ),
        batch = "12234566",
        installments = "10",
        authorization = ReimbursementResponse.Authorization(
            code = "123-12",
            retrievalReferenceNumber = "9876543",
            status = ReimbursementResponse.Status(
                code = "APPROVED",
                situation = ReimbursementResponse.Status.Situation(
                    id = "302",
                    description = "This is a description"
                )
            )
        ),
        displayMessage = ReimbursementResponse.DisplayMessage(
            useCode = "22",
            message = "Necesita Impresion"
        ),
        merchant = ReimbursementResponse.Merchant(
            id = UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"),
        )
    )

fun anAcquirerCustomer() =
    AcquirerCustomer(
        customerId = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        acquirerId = "GPS",
        code = "555"
    )
fun anAcquirerMerchant() =
    AcquirerMerchant(
        merchantId = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        acquirerId = "GPS",
        code = "123"
    )
fun anAcquirerTerminal() =
    AcquirerTerminal(
        terminalId = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        acquirerId = "GPS",
        code = "123"
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = aDate(),
        errors = listOf(
            ApiError(
                code = "401",
                resource = "/payments",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

fun aConstant() = Constants(
    networkInternationalIdentifier = "012",
    pointOfServiceConditionCode = "00",
    handbookVersion = "200",
    hardwareVersion = "abc123",
    auditNumber = "005069"
)

fun aDate() = OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
