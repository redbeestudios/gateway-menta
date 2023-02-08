package com.kiwi.api.payments.hexagonal.application

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentResponse
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerRequest
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerResponse
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.CustomerResponse
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.MerchantResponse
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.TerminalResponse
import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.Country.ARG
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Customer
import com.kiwi.api.payments.hexagonal.domain.Feature
import com.kiwi.api.payments.hexagonal.domain.InputMode
import com.kiwi.api.payments.hexagonal.domain.InputMode.MANUAL
import com.kiwi.api.payments.hexagonal.domain.Origin.ACQUIRER
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.hexagonal.domain.ReversalOperation
import com.kiwi.api.payments.hexagonal.domain.StatusCode
import com.kiwi.api.payments.hexagonal.domain.StatusCode.PENDING
import com.kiwi.api.payments.hexagonal.domain.Terminal
import com.kiwi.api.payments.shared.error.model.ApiErrorResponse
import com.kiwi.api.payments.shared.kafka.ConsumerMessage
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import com.kiwi.api.payments.hexagonal.adapter.out.db.entity.Operation as OperationEntity

val aMerchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aTerminalId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aCustomerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aOperationId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aPaymentId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val acquirerId = "5ed26688389b"
val statusCodeApprove = StatusCode.APPROVED
const val cardPan = "333344445555"
const val cardMaskedPan = "XXXXXXXX5555"

fun aCustomer() =
    Payment.Customer(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = Payment.Customer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = Payment.Customer.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = Payment.Customer.Representative(
            representativeId = Payment.Customer.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = Payment.Customer.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = Payment.Customer.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = Payment.Customer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "ACTIVE"
    )

fun aCustomerReversalOperation() =
    Customer(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
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
        status = "ACTIVE"
    )

fun aMerchant() =
    Payment.Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = Payment.Merchant.Representative(
            id = Payment.Merchant.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = Payment.Merchant.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            ownerId = Payment.Merchant.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = Payment.Merchant.Address(
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
        tax = Payment.Merchant.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = Payment.Merchant.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aMerchantReversalOperation() =
    ReversalOperation.Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = "ARG",
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = ReversalOperation.Merchant.Representative(
            id = ReversalOperation.Merchant.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = ReversalOperation.Merchant.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            id = ReversalOperation.Merchant.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = ReversalOperation.Merchant.Address(
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
        tax = ReversalOperation.Merchant.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = ReversalOperation.Merchant.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aTerminal() =
    Payment.Terminal(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(Feature.CHIP)
    )

fun aTerminalaReversalOperation() =
    ReversalOperation.Terminal(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(Feature.CHIP)
    )

fun aReversalOperaionWithoutOperationId() =
    aReversalOperation().copy(operationId = null)

fun aReversalOperation() =
    ReversalOperation(
        operationId = aPaymentId,
        acquirerId = acquirerId,
        amount = ReversalOperation.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                ReversalOperation.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000",
                )
            )
        ),
        installments = "10",
        trace = "123",
        ticket = "234",
        batch = "111",
        merchant = aMerchantReversalOperation(),
        terminal = aTerminalaReversalOperation(),
        customer = aCustomerReversalOperation(),
        capture = ReversalOperation.Capture(
            card = ReversalOperation.Capture.Card(
                holder = ReversalOperation.Capture.Card.Holder(
                    name = "sebastian",
                    identification = ReversalOperation.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                pan = cardPan,
                expirationDate = "0622",
                cvv = "234",
                track1 = "track 1",
                track2 = "track2",
                iccData = "data",
                cardSequenceNumber = "card sequence",
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA",
                pin = "000",
                ksn = "456"
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = null
        ),
        datetime = aDate()
    )

val aTerminalResponse = Terminal(
    id = UUID.fromString("0ce8dc16-da32-4dbb-9acc-c29ed52c6aca"),
    merchantId = UUID.fromString("3f30f5f7-c85a-4769-ae99-a76545895481"),
    customerId = UUID.fromString("2c6acc4f-14cd-445a-99a5-1d80c70cc6ed"),
    serialCode = "50010003",
    hardwareVersion = "10",
    tradeMark = "pirutchit",
    model = "zg300",
    status = "ACTIVE",
    features = listOf(Feature.MANUAL, Feature.STRIPE, Feature.CHIP)
)

val aCustomerResponse = Payment.Customer(
    id = UUID.fromString("2c6acc4f-14cd-445a-99a5-1d80c70cc6ed"),
    country = "ARG",
    legalType = "NATURAL_PERSON",
    businessName = "PedidosYa",
    fantasyName = "PEYA (GPS - Flujo Directo)",
    tax = Payment.Customer.Tax("something", "1123123"),
    activity = "comida",
    email = "emix.lr@gmail.com",
    phone = "111123213132",
    address = Payment.Customer.Address("villa crespo", "Bs As", "1889", "23", "27", "", ""),
    representative = Payment.Customer.Representative(
        representativeId = Payment.Customer.Representative.RepresentativeId(
            type = "dni",
            number = "32456789"
        ),
        birthDate = OffsetDateTime.parse("2022-03-31T15:01:01.999Z"),
        name = "Soy un nombre de represent",
        surname = "soy un apellido de represen"
    ),
    businessOwner = null,
    settlementCondition = null,
    status = "ACTIVE"
)

fun aReceivedTerminal() =
    Terminal(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(Feature.CHIP)
    )

fun aPaymentRequest() =
    PaymentRequest(
        amount = PaymentRequest.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                PaymentRequest.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000"
                )
            )
        ),
        installments = "10",
        trace = "123",
        ticket = "234",
        terminal = PaymentRequest.Terminal(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            softwareVersion = "2.0"
        ),
        capture = PaymentRequest.Capture(
            card = PaymentRequest.Capture.Card(
                holder = PaymentRequest.Capture.Card.Holder(
                    name = "sebastian",
                    identification = PaymentRequest.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                pan = cardPan,
                expirationDate = "0622",
                cvv = "234",
                track1 = "track 1",
                track2 = "track2",
                iccData = "data",
                cardSequenceNumber = "card sequence",
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA",
                pin = "000",
                ksn = "456"
            ),
            inputMode = MANUAL
        ),
        batch = "111",
        datetime = aDate()
    )

fun aCreatedPayment() =
    CreatedPayment(
        id = aPaymentId,
        ticketId = aPaymentId.hashCode(),
        origin = ACQUIRER,
        authorization = anAuthorization(),
        data = aPayment()
    )

fun aCreatedPaymentWithMaskedPan() =
    CreatedPayment(
        id = aPaymentId,
        ticketId = aPaymentId.hashCode(),
        origin = ACQUIRER,
        authorization = anAuthorization(),
        data = aPaymentWithMaskedPan()
    )

fun anAuthorization(statusCode: StatusCode = statusCodeApprove) =
    Authorization(
        status = Authorization.Status(
            code = statusCode,
            situation = Authorization.Status.Situation(
                id = "id",
                description = "description"
            )
        ),
        retrievalReferenceNumber = "456",
        authorizationCode = "123-123",
        displayMessage = "a message"
    )

fun aPayment() =
    Payment(
        amount = Payment.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                Payment.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000",
                )
            )
        ),
        installments = "10",
        trace = "123",
        ticket = "234",
        batch = "111",
        merchant = aMerchant(),
        terminal = aTerminal(),
        customer = aCustomer(),
        capture = Payment.Capture(
            card = Payment.Capture.Card(
                holder = Payment.Capture.Card.Holder(
                    name = "sebastian",
                    identification = Payment.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                pan = cardPan,
                expirationDate = "0622",
                cvv = "234",
                track1 = "track 1",
                track2 = "track2",
                iccData = "data",
                cardSequenceNumber = "card sequence",
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA",
                pin = "000",
                ksn = "456"
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = null
        ),
        datetime = aDate(),
    )

fun aPaymentResponse() =
    PaymentResponse(
        id = aPaymentId,
        ticketId = aPaymentId.hashCode(),
        status = PaymentResponse.Status(
            code = statusCodeApprove,
            situation = PaymentResponse.Status.Situation(
                id = "id",
                description = "description"
            )
        ),
        authorization = PaymentResponse.Authorization(
            code = "123-123",
            retrievalReferenceNumber = "456",
            displayMessage = "a message"
        ),
        datetime = aDate(),
        amount = PaymentResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(PaymentResponse.Amount.Breakdown(description = "descripcion", amount = "1000"))
        ),
        installments = "10",
        trace = "123",
        ticket = "234",
        merchant = PaymentResponse.Merchant(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
        ),
        terminal = PaymentResponse.Terminal(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
            serialCode = "1234",
            softwareVersion = "2.0",
        ),
        capture = PaymentResponse.Capture(
            inputMode = MANUAL,
            previousTransactionInputMode = null,
            card = PaymentResponse.Capture.Card(
                holder = PaymentResponse.Capture.Card.Holder(
                    name = "sebastian",
                    identification = PaymentResponse.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                iccData = "data",
                maskedPan = cardMaskedPan,
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA"
            )
        ),
        hostMessage = "host message",
        batch = "111"
    )

fun anAcquirerRequest() =
    AcquirerRequest(
        capture = AcquirerRequest.Capture(
            card = AcquirerRequest.Capture.Card(
                holder = AcquirerRequest.Capture.Card.Holder(
                    name = "sebastian",
                    identification = AcquirerRequest.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                pan = cardPan,
                expirationDate = "0622",
                cvv = "234",
                track1 = "track 1",
                track2 = "track2",
                pin = "000",
                emv = AcquirerRequest.Capture.Card.EMV(
                    iccData = "data",
                    cardSequenceNumber = "card sequence",
                    ksn = "456"
                ),
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA"
            ),
            inputMode = "MANUAL",
            previousTransactionInputMode = null
        ),
        amount = AcquirerRequest.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                AcquirerRequest.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000"
                )
            )
        ),
        datetime = aDate(),
        trace = "123",
        ticket = "234",
        terminal = AcquirerRequest.Terminal(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            serialCode = "1234",
            hardwareVersion = "2.0",
            softwareVersion = "2.0",
            tradeMark = "1234",
            model = "1234",
            status = "ACTIVE",
            features = listOf(Feature.CHIP)
        ),
        merchant = AcquirerRequest.Merchant(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            country = ARG,
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
            address = AcquirerRequest.Customer.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
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
        batch = "111",
        installments = "10"
    )

fun anAcquirerResponse() =
    AcquirerResponse(
        capture = AcquirerResponse.Capture(
            card = AcquirerResponse.Capture.Card(
                holder = AcquirerResponse.Capture.Card.Holder(
                    name = "sebastian",
                    identification = AcquirerResponse.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                iccData = null,
                maskedPan = cardMaskedPan,
                bank = "SANTANDER",
                type = "DEBIT",
                brand = "VISA",
                workingKey = null,
                nationality = null
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = null
        ),
        amount = AcquirerResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                AcquirerResponse.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000"
                )
            )
        ),
        datetime = aDate(),
        trace = "123",
        ticket = "234",
        terminal = AcquirerResponse.Terminal(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
            serialCode = "1234",
            softwareVersion = "2.0",
            features = listOf(Feature.CHIP)
        ),
        merchant = AcquirerResponse.Merchant(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
        ),
        batch = "111",
        installments = "10",
        authorization = AcquirerResponse.Authorization(
            status = AcquirerResponse.Status(
                code = statusCodeApprove,
                situation = AcquirerResponse.Status.Situation(
                    id = "id",
                    description = "description"
                )
            ),
            retrievalReferenceNumber = "456",
            code = "123-123"
        ),
        displayMessage = AcquirerResponse.DisplayMessage(
            useCode = "useCode",
            message = "a message"
        )
    )

fun aCustomerResponse() =
    CustomerResponse(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
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
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(Feature.CHIP)
    )

fun aMerchantResponse() =
    MerchantResponse(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = MerchantResponse.Representative(
            representativeId = MerchantResponse.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = MerchantResponse.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            ownerId = MerchantResponse.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = MerchantResponse.Address(
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
        settlementCondition = MerchantResponse.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun emvPayment() =
    aPayment().copy(
        capture = aPayment().capture.copy(
            inputMode = InputMode.EMV,
            card = aPayment().capture.card.copy(
                iccData = "data"
            )
        )
    )

fun emvCreatedPayment() =
    aCreatedPayment().copy(
        data = aCreatedPayment().data.copy(
            capture = aCreatedPayment().data.capture.copy(
                inputMode = InputMode.EMV,
                card = aCreatedPayment().data.capture.card.copy(
                    iccData = "data"
                )
            )
        )

    )

fun aCreatedPaymentPending() =
    aCreatedPayment().copy(
        authorization = anAuthorization(PENDING)
    )

fun aPaymentWithMaskedPan() =
    aPayment().copy(
        capture = aPayment().capture.copy(
            card = aPayment().capture.card.copy(
                pan = cardMaskedPan
            )
        )
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = aDate(),
        errors = listOf(
            ApiErrorResponse.ApiError(
                code = 401,
                resource = "/payments",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

fun aCard() = Payment.Capture.Card(
    holder = Payment.Capture.Card.Holder(
        name = "sebastian",
        identification = Payment.Capture.Card.Holder.Identification(
            number = "444444",
            type = "DNI"
        )
    ),
    pan = cardMaskedPan,
    expirationDate = "0622",
    cvv = "234",
    track1 = "track 1",
    track2 = "track2",
    iccData = "data",
    cardSequenceNumber = "card sequence",
    bank = "SANTANDER",
    type = "DEBIT",
    brand = "VISA",
    pin = "000",
    ksn = "456"
)

fun aOperationEntityWithStatusReversal() =
    OperationEntity(
        id = aOperationId,
        ticketId = aOperationId.hashCode(),
        acquirerId = anAuthorization().retrievalReferenceNumber,
        origin = ACQUIRER,
        authorizationCode = anAuthorization().authorizationCode,
        displayMessage = anAuthorization().displayMessage,
        statusCode = StatusCode.REVERSAL.name,
        situationCode = anAuthorization().status.situation?.id,
        situationMessage = anAuthorization().status.situation?.description,
        merchantId = aMerchantId,
        terminalId = aTerminalId,
        customerId = aCustomerId,
        amount = "10000",
        currency = "ARS",
        trace = "123",
        ticket = "234",
        batch = "111",
        installments = "10",
        identityType = aCard().holder.identification?.type,
        identityNumber = aCard().holder.identification?.number,
        inputMode = MANUAL,
        cardHolderName = aCard().holder.name,
        cardBrand = aCard().brand,
        cardBank = aCard().bank,
        cardType = aCard().type,
        cardPan = aCard().pan,
        tip = null,
        advance = null,
        datetime = aDate(),
        createDatetime = aDate(),
    )

fun aOperationEntity() =
    OperationEntity(
        id = aOperationId,
        ticketId = aOperationId.hashCode(),
        acquirerId = anAuthorization().retrievalReferenceNumber,
        origin = ACQUIRER,
        authorizationCode = anAuthorization().authorizationCode,
        displayMessage = anAuthorization().displayMessage,
        statusCode = anAuthorization().status.code.name,
        situationCode = anAuthorization().status.situation?.id,
        situationMessage = anAuthorization().status.situation?.description,
        merchantId = aMerchantId,
        terminalId = aTerminalId,
        customerId = aCustomerId,
        amount = "10000",
        currency = "ARS",
        trace = "123",
        ticket = "234",
        batch = "111",
        installments = "10",
        identityType = aCard().holder.identification?.type,
        identityNumber = aCard().holder.identification?.number,
        inputMode = MANUAL,
        cardHolderName = aCard().holder.name,
        cardBrand = aCard().brand,
        cardBank = aCard().bank,
        cardType = aCard().type,
        cardPan = aCard().pan,
        tip = null,
        advance = null,
        datetime = aDate(),
        createDatetime = aDate(),
    )

fun <T> aConsumerMessage(message: T) =
    ConsumerMessage(
        message = message,
        key = "a key",
        topic = "a topic",
        partitionId = null
    )

private fun aDate() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
