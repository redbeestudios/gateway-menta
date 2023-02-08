package com.kiwi.api.reimbursements.hexagonal.application

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.ReimbursementResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.entity.Operation
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.CustomerResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.Address
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.BusinessOwner
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.BusinessOwner.OwnerId
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.Representative
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.Representative.RepresentativeId
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse.SettlementCondition
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.TerminalResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.TransactionResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import com.kiwi.api.reimbursements.hexagonal.domain.Feature.CHIP
import com.kiwi.api.reimbursements.hexagonal.domain.Feature.CONTACTLESS
import com.kiwi.api.reimbursements.hexagonal.domain.InputMode.MANUAL
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import com.kiwi.api.reimbursements.hexagonal.domain.OperationType
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.Reimbursement
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import com.kiwi.api.reimbursements.shared.error.model.ApiError
import com.kiwi.api.reimbursements.shared.error.model.ApiErrorResponse
import com.kiwi.api.reimbursements.shared.kafka.ConsumerMessage
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

val paymentId = UUID.fromString("bb0188f8-b97e-4047-b695-dc5a3720ec44")
const val acquirerId = "1234567890"
val id: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
const val holderName = "Aixa Halac"
const val identificationNumber = "35727828"
const val identificationType = "DNI"
const val currency = "ARS"
const val terminalSerialCode = "134"
val terminalId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val transactionId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aOperationId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
const val totalAmount = "100.00"
const val breakdownDescription = "description"
const val breakdownAmount = "1000"
const val installments = "10"
const val trace = "123"
const val ticket = "234"
const val cardPan = "333344445555"
const val cardExpirationDate = "06/22"
const val cardCVV = "234"
const val cardIccData = "710F860D84240000083C81B030F10FB6D6910A1256D011FDC6C9350012"
const val inputMode = "MANUAL"
const val batch = "12434"
const val hostMessage = "hostMessage"
const val authorizationCode = "4002"
const val retrievalReferenceNumber = "1234567890"
const val cardMaskedPan = "XXXXXXXX5555"
const val cardSequenceNumber = "20304504"
const val cardBank = "COMAFI"
const val cardType = "CREDIT"
const val cardBrand = "VISA"
const val track1 = "track1"
const val track2 = "track2"
const val STATUS_APPROVED = "APPROVED"
const val softwareVersion = "1.0.456"
const val hardwareVersion = "1.0.456"
const val pin = "123"
const val ksn = "456"
const val previousTransactionInputMode = "1111"
const val tradeMark = "1111"
const val model = "uno"
const val status = "ACTIVE"
const val statusCode = "APPROVED"
const val situationId = "00"
const val situationDescription = "approve"
const val displayMessageCode = "00"
const val displayMessage = "Imprimir"

val features = listOf(CHIP, CONTACTLESS)

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

fun aReimbursementRequest() =
    ReimbursementRequest(
        paymentId = paymentId,
        acquirerId = acquirerId,
        datetime = datetime.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
        capture = ReimbursementRequest.Capture(
            card = ReimbursementRequest.Capture.Card(
                holder = ReimbursementRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = ReimbursementRequest.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                pan = cardPan,
                expirationDate = cardExpirationDate,
                cvv = cardCVV,
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                pin = pin,
                ksn = ksn,
                track1 = track1,
                track2 = track2
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        trace = trace,
        ticket = ticket,
        terminal = ReimbursementRequest.Terminal(
            id = terminalId,
            softwareVersion = softwareVersion
        ),
        amount = ReimbursementRequest.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                ReimbursementRequest.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        batch = batch,
    )

fun anAnnulment() =
    Annulment(
        paymentId = paymentId,
        acquirerId = acquirerId,
        merchant = aMerchant(),
        customer = aCustomer(),
        terminal = aReimbursementTerminal(),
        capture = Reimbursement.Capture(
            card = Reimbursement.Capture.Card(
                holder = Reimbursement.Capture.Card.Holder(
                    name = holderName,
                    identification = Reimbursement.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                pan = cardPan,
                expirationDate = cardExpirationDate,
                cvv = cardCVV,
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                track1 = track1,
                track2 = track2,
                pin = pin,
                ksn = ksn
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        amount = Reimbursement.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Reimbursement.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        datetime = datetime,
    )

fun aRefund() =
    Refund(
        paymentId = paymentId,
        acquirerId = acquirerId,
        merchant = aMerchant(),
        terminal = aReimbursementTerminal(),
        customer = aCustomer(),
        capture = Reimbursement.Capture(
            card = Reimbursement.Capture.Card(
                holder = Reimbursement.Capture.Card.Holder(
                    name = holderName,
                    identification = Reimbursement.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                pan = cardPan,
                expirationDate = cardExpirationDate,
                cvv = cardCVV,
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                track1 = track1,
                track2 = track2,
                pin = pin,
                ksn = ksn
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        amount = Reimbursement.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Reimbursement.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        datetime = datetime
    )

fun aCreatedRefund() =
    CreatedRefund(
        id = id,
        data = aRefund(),
        authorization = anAuthorization()
    )

fun aCreatedAnnulment() =
    CreatedAnnulment(
        id = id,
        authorization = anAuthorization(),
        data = anAnnulment()
    )

fun aReimbursementResponse() =
    ReimbursementResponse(
        id = id.toString(),
        acquirerId = acquirerId,
        status = ReimbursementResponse.Status(
            code = STATUS_APPROVED,
            situation = null
        ),
        authorization = ReimbursementResponse.Authorization(
            code = authorizationCode,
            retrievalReferenceNumber = retrievalReferenceNumber,
            displayMessage = null
        ),
        datetime = datetime,
        amount = ReimbursementResponse.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                ReimbursementResponse.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        trace = trace,
        ticket = ticket,
        merchant = ReimbursementResponse.Merchant(
            id = merchantId,
        ),
        terminal = ReimbursementResponse.Terminal(
            id = terminalId,
            serialCode = terminalSerialCode,
            softwareVersion = softwareVersion
        ),
        capture = ReimbursementResponse.Capture(
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode,
            card = ReimbursementResponse.Capture.Card(
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                holder = ReimbursementResponse.Capture.Card.Holder(
                    name = holderName,
                    identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                maskedPan = cardMaskedPan,
                iccData = cardIccData
            )
        ),
        hostMessage = hostMessage,
        batch = batch,
        installments = installments
    )

fun anAuthorization() =
    Authorization(
        authorizationCode = authorizationCode,
        retrievalReferenceNumber = retrievalReferenceNumber,
        status = Authorization.Status(
            code = statusCode,
            situation = Authorization.Status.Situation(
                id = situationId,
                description = situationDescription
            )
        ),
        displayMessage = Authorization.DisplayMessage(
            useCode = displayMessageCode,
            message = displayMessage
        )
    )

fun aMerchant() =
    Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = "ARG",
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = Merchant.Representative(
            id = Merchant.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = Merchant.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            id = Merchant.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = Merchant.Address(
            state = "state",
            city = "city",
            zip = "zip",
            street = "street",
            number = "number",
            floor = "floor",
            apartment = "apartment"
        ),
        email = "email",
        phone = "phone",
        activity = "activity",
        category =  "7372",
        tax = Merchant.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = Merchant.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aReimbursementTerminal() = Reimbursement.Terminal(
    id = terminalId,
    merchantId = merchantId,
    customerId = customerId,
    serialCode = terminalSerialCode,
    hardwareVersion = hardwareVersion,
    softwareVersion = softwareVersion,
    features = features,
    tradeMark = tradeMark,
    model = model,
    status = status
)

fun aTerminal() = Terminal(
    id = terminalId,
    merchantId = merchantId,
    customerId = customerId,
    serialCode = terminalSerialCode,
    hardwareVersion = hardwareVersion,
    features = features,
    tradeMark = tradeMark,
    model = model,
    status = status
)

fun aCustomer() =
    Customer(
        id = customerId,
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = Customer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = Customer.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = Customer.Representative(
            representativeId = Customer.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = Customer.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = Customer.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = Customer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = status
    )

fun aTransaction() =
    Transaction(
        id = transactionId
    )

fun aCustomerResponse() =
    CustomerResponse(
        id = customerId,
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = CustomerResponse.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = CustomerResponse.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = CustomerResponse.Representative(
            representativeId = CustomerResponse.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = CustomerResponse.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = CustomerResponse.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = CustomerResponse.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "ACTIVE"
    )

fun aTerminalResponse() =
    TerminalResponse(
        id = terminalId,
        merchantId = merchantId,
        customerId = customerId,
        serialCode = terminalSerialCode,
        hardwareVersion = hardwareVersion,
        tradeMark = tradeMark,
        model = model,
        status = status,
        features = features
    )

fun aMerchantResponse() =
    MerchantResponse(
        id = merchantId,
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = "ARG",
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = Representative(
            representativeId = RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            ownerId = OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = Address(
            state = "state",
            city = "city",
            zip = "zip",
            street = "street",
            number = "number",
            floor = "floor",
            apartment = "apartment"
        ),
        email = "email",
        phone = "phone",
        activity = "activity",
        category = "7372",
        tax = MerchantResponse.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aTransactionResponse() =
    TransactionResponse(
        id = transactionId
    )

fun anAcquirerRequest() =
    AcquirerRequest(
        capture = AcquirerRequest.Capture(
            card = AcquirerRequest.Capture.Card(
                holder = AcquirerRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = AcquirerRequest.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                pan = cardPan,
                expirationDate = cardExpirationDate,
                cvv = cardCVV,
                track1 = track1,
                track2 = track2,
                pin = pin,
                emv = AcquirerRequest.Capture.Card.EMV(
                    iccData = cardIccData,
                    cardSequenceNumber = cardSequenceNumber,
                    ksn = ksn
                ),
                bank = cardBank,
                type = cardType,
                brand = cardBrand
            ),
            inputMode = inputMode,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        amount = AcquirerRequest.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                AcquirerRequest.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        datetime = datetime,
        trace = trace,
        ticket = ticket,
        terminal = AcquirerRequest.Terminal(
            id = terminalId,
            merchantId = merchantId,
            customerId = customerId,
            serialCode = terminalSerialCode,
            hardwareVersion = hardwareVersion,
            softwareVersion = softwareVersion,
            tradeMark = tradeMark,
            model = model,
            status = status,
            features = features
        ),
        merchant = AcquirerRequest.Merchant(
            id = merchantId,
            customerId = customerId,
            country = "ARG",
            legalType = "legal type",
            businessName = "business name",
            fantasyName = "fantasy name",
            representative = AcquirerRequest.Merchant.Representative(
                id = AcquirerRequest.Merchant.Representative.RepresentativeId(
                    type = "type",
                    number = "number"
                ),
                birthDate = "2022-03-31T22:01:01.999+07:00",
                name = "name",
                surname = "surname"
            ),
            businessOwner = AcquirerRequest.Merchant.BusinessOwner(
                name = "name",
                surname = "surname",
                birthDate = "birth date",
                id = AcquirerRequest.Merchant.BusinessOwner.OwnerId(
                    type = "type",
                    number = "number"
                )
            ),
            merchantCode = "merchant code",
            address = AcquirerRequest.Merchant.Address(
                state = "state",
                city = "city",
                zip = "zip",
                street = "street",
                number = "number",
                floor = "floor",
                apartment = "apartment"
            ),
            email = "email",
            phone = "phone",
            activity = "activity",
            category = "7372",
            tax = AcquirerRequest.Merchant.Tax(
                id = "id",
                type = "a type"
            ),
            settlementCondition = AcquirerRequest.Merchant.SettlementCondition(
                transactionFee = "0",
                settlement = "settlement",
                cbuOrCvu = "cbu 111111111"
            )
        ),
        customer = AcquirerRequest.Customer(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = "ARG",
            legalType = "NATURAL_PERSON",
            businessName = "a business name",
            fantasyName = "a fantasy name",
            tax = AcquirerRequest.Customer.Tax("a type", "a tax id"),
            activity = "an activity",
            email = "hola@hola.com",
            phone = "1234567890",
            address = AcquirerRequest.Customer.Address(
                "a state",
                "a city",
                "a zip",
                "a street",
                "a number",
                "",
                ""
            ),
            representative = AcquirerRequest.Customer.Representative(
                representativeId = AcquirerRequest.Customer.Representative.RepresentativeId(
                    type = "DNI",
                    number = "99999999"
                ),
                birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
                name = "Jose",
                surname = "Perez"
            ),
            businessOwner = AcquirerRequest.Customer.BusinessOwner(
                name = "Pedro",
                surname = "Gonzalez",
                birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
                ownerId = AcquirerRequest.Customer.BusinessOwner.OwnerId(
                    type = "DNI",
                    number = "99888777"
                )
            ),
            settlementCondition = AcquirerRequest.Customer.SettlementCondition(
                transactionFee = "transactionFee",
                settlement = "settlement",
                cbuOrCvu = "cbuOrCvu"
            ),
            status = "ACTIVE"
        ),
        batch = batch,
        installments = installments,
        retrievalReferenceNumber = retrievalReferenceNumber
    )

fun anAcquirerResponse() =
    AcquirerResponse(
        capture = AcquirerResponse.Capture(
            card = AcquirerResponse.Capture.Card(
                holder = AcquirerResponse.Capture.Card.Holder(
                    name = holderName,
                    identification = AcquirerResponse.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                iccData = cardIccData,
                maskedPan = cardMaskedPan,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                workingKey = null,
                nationality = null
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        amount = AcquirerResponse.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                AcquirerResponse.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        datetime = datetime,
        trace = trace,
        ticket = ticket,
        terminal = AcquirerResponse.Terminal(
            id = terminalId,
            serialCode = terminalSerialCode,
            softwareVersion = softwareVersion,
            features = features
        ),
        merchant = AcquirerResponse.Merchant(
            id = merchantId
        ),
        batch = batch,
        installments = installments,
        authorization = AcquirerResponse.Authorization(
            status = AcquirerResponse.Status(
                code = STATUS_APPROVED,
                situation = AcquirerResponse.Status.Situation(
                    id = situationId,
                    description = situationDescription
                )
            ),
            retrievalReferenceNumber = retrievalReferenceNumber,
            code = authorizationCode
        ),
        displayMessage = AcquirerResponse.DisplayMessage(
            useCode = displayMessageCode,
            message = displayMessage
        )
    )

fun aCard() = Reimbursement.Capture.Card(
    holder = Reimbursement.Capture.Card.Holder(
        name = holderName,
        identification = Reimbursement.Capture.Card.Holder.Identification(
            number = identificationNumber,
            type = identificationType
        )
    ),
    pan = cardPan,
    expirationDate = cardExpirationDate,
    cvv = cardCVV,
    track1 = track1,
    track2 = track2,
    iccData = cardIccData,
    cardSequenceNumber = cardSequenceNumber,
    bank = cardBank,
    type = cardType,
    brand = cardBrand,
    pin = pin,
    ksn = ksn
)

fun aOperation(operationType: OperationType) =
    Operation(
        id = aOperationId,
        paymentId = paymentId,
        acquirerId = anAuthorization().retrievalReferenceNumber,
        type = operationType.name,
        authorizationCode = anAuthorization().authorizationCode,
        displayMessage = anAuthorization().displayMessage?.message,
        statusCode = anAuthorization().status.code,
        situationCode = anAuthorization().status.situation?.id,
        situationMessage = anAuthorization().status.situation?.description,
        merchantId = merchantId,
        terminalId = terminalId,
        customerId = customerId,
        amount = totalAmount,
        currency = currency,
        trace = trace,
        ticket = ticket,
        batch = batch,
        installments = installments,
        identityType = aCard().holder.identification?.type,
        identityNumber = aCard().holder.identification?.number,
        inputMode = MANUAL,
        cardHolderName = aCard().holder.name,
        cardBrand = aCard().brand,
        cardBank = aCard().bank,
        cardType = aCard().type,
        cardPan = cardMaskedPan,
        tip = null,
        advance = null,
        operationDatetime = aDatetime(),
        createDatetime = aDatetime(),
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = datetime,
        errors = listOf(
            ApiError(
                code = 401,
                resource = "/reimbursements",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

fun <T> aConsumerMessage(message: T) =
    ConsumerMessage(
        message = message,
        key = "a key",
        topic = "a topic",
        partitionId = null
    )

fun aDatetime() =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))
