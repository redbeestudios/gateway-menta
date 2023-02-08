package com.kiwi.api.reversal.hexagonal.application

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementResponse
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.CustomerResponse
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.TerminalResponse
import com.kiwi.api.reversal.hexagonal.domain.Country.ARG
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import com.kiwi.api.reversal.hexagonal.domain.entities.Feature.CHIP
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import com.kiwi.api.reversal.hexagonal.domain.entities.Terminal
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedRefund
import com.kiwi.api.reversal.hexagonal.domain.operations.InputMode.MANUAL
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType.ANNULMENT
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType.PAYMENT
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType.REFUND
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.hexagonal.domain.operations.Reimbursement.Amount
import com.kiwi.api.reversal.hexagonal.domain.operations.Reimbursement.Capture
import com.kiwi.api.reversal.shared.error.model.ApiError
import com.kiwi.api.reversal.shared.error.model.ApiErrorResponse
import com.kiwi.api.reversal.shared.kafka.ConsumerMessage
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import com.kiwi.api.reversal.hexagonal.adapter.out.db.entity.Operation as OperationEntity

const val operationId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
const val acquirerId = "1234567890"
const val id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
const val holderName = "sebastian"
const val identificationNumber = "444444"
const val identificationType = "DNI"
const val currency = "ARS"
const val terminalSerialCode = "1234"
const val merchantId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
const val totalAmount = "10000"
const val breakdownDescription = "description"
const val breakdownAmount = "1000"
const val installments = "10"
const val trace = "123"
const val ticket = "234"
const val cardPan = "333344445555"
const val cardExpirationDate = "0622"
const val cardCVV = "234"
const val cardIccData = "iccData"
const val batch = "111"
const val hostMessage = "hostMessage"
const val authorizationCode = "4002"
const val retrievalReferenceNumber = acquirerId
const val cardMaskedPan = "XXXXXXXX5555"
const val cardSequenceNumber = "20304504"
const val cardBank = "SANTANDER"
const val cardType = "CREDIT"
const val cardBrand = "VISA"
const val cardInputMode = "MANUAL"
const val track1 = "track1"
const val track2 = "track2"
const val STATUS_APPROVED = "APPROVED"
const val softwareVersion = "2.0"
const val pin = "123"
const val ksn = "456"
const val previousTransactionInputMode = "MANUAL"
const val terminalId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
const val iccData = "iccData"
const val displayMessage = "a message"
val aMerchantId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aTerminalId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aCustomerId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aOperationId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

val batchCloseTotals = listOf(
    BatchCloseRequest.Total(
        operationCode = PAYMENT,
        amount = "123456700",
        currency = currency
    ),
    BatchCloseRequest.Total(
        operationCode = ANNULMENT,
        amount = "234500",
        currency = currency
    ),
    BatchCloseRequest.Total(
        operationCode = REFUND,
        amount = "78900",
        currency = currency
    )
)

fun aCustomer() =
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
    Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
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
        category = "7372",
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

fun aTerminal() =
    Terminal(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(CHIP)
    )

fun aReceivedTerminal() =
    ReceivedTerminal(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        serialCode = "1234",
        hardwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(CHIP)
    )

fun aPaymentRequest() =
    PaymentRequest(
        operationId = aOperationId,
        acquirerId = acquirerId,
        amount = PaymentRequest.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                PaymentRequest.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        terminal = PaymentRequest.Terminal(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            softwareVersion = "2.0"
        ),
        capture = PaymentRequest.Capture(
            card = PaymentRequest.Capture.Card(
                holder = PaymentRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = PaymentRequest.Capture.Card.Holder.Identification(
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
                track2 = track2,
                track1 = track1
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = "MANUAL"
        ),
        batch = batch,
        datetime = aDatetime()
    )

fun aReimbursementRequest() =
    ReimbursementRequest(
        operationId = aOperationId,
        acquirerId = acquirerId,
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
        trace = trace,
        ticket = ticket,
        terminal = ReimbursementRequest.Terminal(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            softwareVersion = "2.0"
        ),
        capture = ReimbursementRequest.Capture(
            card = ReimbursementRequest.Capture.Card(
                holder = ReimbursementRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = ReimbursementRequest.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType,
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
            previousTransactionInputMode = "MANUAL"
        ),
        batch = batch,
        datetime = aDatetime()
    )

fun aBatchCloseRequest() =
    BatchCloseRequest(
        datetime = aDatetime(),
        trace = trace,
        ticket = ticket,
        batch = batch,
        terminal = BatchCloseRequest.Terminal(
            id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
            softwareVersion = "2.0"
        ),
        totals = batchCloseTotals
    )

fun aPayment() =
    Payment(
        operationId = aOperationId,
        acquirerId = acquirerId,
        amount = Payment.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Payment.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = aMerchant(),
        terminal = aTerminal(),
        customer = aCustomer(),
        capture = Payment.Capture(
            card = Payment.Capture.Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardExpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                pin = pin,
                ksn = ksn,
                holder = Payment.Capture.Card.Holder(
                    name = holderName,
                    identification = Payment.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                )
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        datetime = aDatetime(),
    )

fun anAnnulment() =
    Annulment(
        operationId = aOperationId,
        acquirerId = acquirerId,
        amount = Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = aMerchant(),
        terminal = aTerminal(),
        customer = aCustomer(),
        capture = Capture(
            card = Capture.Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardExpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                pin = pin,
                ksn = ksn,
                holder = Capture.Card.Holder(
                    name = holderName,
                    identification = Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = "MANUAL"
        ),
        datetime = aDatetime(),
    )

fun aRefund() =
    Refund(
        operationId = aOperationId,
        acquirerId = acquirerId,
        amount = Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = aMerchant(),
        terminal = aTerminal(),
        customer = aCustomer(),
        capture = Capture(
            card = Capture.Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardExpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                pin = pin,
                ksn = ksn,
                holder = Capture.Card.Holder(
                    name = holderName,
                    identification = Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode
        ),
        datetime = aDatetime(),
    )

fun anAuthorization() =
    Authorization(
        authorizationCode = "4002",
        status = Authorization.Status(
            code = "APPROVED",
            situation = Authorization.Status.Situation(
                id = "id",
                description = "description"
            )
        ),
        retrievalReferenceNumber = retrievalReferenceNumber,
        displayMessage = displayMessage
    )

fun aCreatedPayment() =
    CreatedPayment(
        id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
        authorization = anAuthorization(),
        data = aPayment()
    )

fun aBatchClose() =
    BatchClose(
        id = "id",
        authorization = BatchClose.Authorization(
            code = "200",
            displayMessage = "displaymessage",
            status = BatchClose.Authorization.Status(
                code = "00",
            ),
            retrievalReferenceNumber = "1456"
        ),
        merchant = aMerchant(),
        terminal = aTerminal(),
        customer = aCustomer(),
        trace = "123",
        ticket = "234",
        batch = "111",
        hostMessage = "hostMessage",
        datetime = aDatetime(),
        totals = listOf(
            BatchClose.Total(
                operationCode = PAYMENT,
                amount = "123456700",
                currency = "ARS"
            ),
            BatchClose.Total(
                operationCode = ANNULMENT,
                amount = "234500",
                currency = "ARS"
            ),
            BatchClose.Total(
                operationCode = REFUND,
                amount = "78900",
                currency = "ARS"
            )
        )
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

fun aPaymentResponse() =
    PaymentResponse(
        operationId = operationId,
        datetime = aDatetime(),
        amount = PaymentResponse.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                PaymentResponse.Amount.Breakdown(
                    description = breakdownDescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        merchant = PaymentResponse.Merchant(
            id = merchantId,
        ),
        terminal = PaymentResponse.Terminal(
            id = terminalId,
            serialCode = terminalSerialCode,
            softwareVersion = softwareVersion
        ),
        capture = PaymentResponse.Capture(
            inputMode = MANUAL,
            previousTransactionInputMode = previousTransactionInputMode,
            card = PaymentResponse.Capture.Card(
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                holder = PaymentResponse.Capture.Card.Holder(
                    name = holderName,
                    identification = PaymentResponse.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                maskedPan = cardMaskedPan,
                iccData = iccData
            )
        ),
        hostMessage = hostMessage,
        batch = batch
    )

fun aReimbursementResponse() =
    ReimbursementResponse(
        operationId = operationId,
        datetime = aDatetime(),
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
                iccData = iccData
            )
        ),
        hostMessage = hostMessage,
        batch = batch,
        installments = installments
    )

fun aBatchCloseResponse() =
    BatchCloseResponse(
        id = id,
        status = BatchCloseResponse.Status(code = STATUS_APPROVED, situation = null),
        authorization = BatchCloseResponse.Authorization(
            code = authorizationCode,
            retrievalReferenceNumber = retrievalReferenceNumber,
            displayMessage = null
        ),
        merchant = BatchCloseResponse.Merchant(
            id = merchantId
        ),
        terminal = BatchCloseResponse.Terminal(
            id = terminalId,
            serialCode = terminalSerialCode,
            softwareVersion = softwareVersion
        ),
        trace = trace,
        ticket = ticket,
        batch = batch,
        hostMessage = hostMessage,
        datetime = aDatetime(),
        totals = listOf(
            BatchCloseResponse.Total(
                operationCode = PAYMENT,
                amount = "123456700",
                currency = "ARS"
            ),
            BatchCloseResponse.Total(
                operationCode = ANNULMENT,
                amount = "234500",
                currency = "ARS"
            ),
            BatchCloseResponse.Total(
                operationCode = REFUND,
                amount = "78900",
                currency = "ARS"
            )
        )
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
                pan = "333344445555",
                expirationDate = "0622",
                cvv = "234",
                track1 = "track1",
                track2 = "track2",
                pin = "123",
                emv = AcquirerRequest.Capture.Card.EMV(
                    iccData = "iccData",
                    cardSequenceNumber = "20304504",
                    ksn = "456"
                ),
                bank = "SANTANDER",
                type = "CREDIT",
                brand = "VISA"
            ),
            inputMode = "MANUAL",
            previousTransactionInputMode = "MANUAL"
        ),
        amount = AcquirerRequest.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                AcquirerRequest.Amount.Breakdown(
                    description = "description",
                    amount = "1000"
                )
            )
        ),
        datetime = aDatetime(),
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
            features = listOf(CHIP)
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
        installments = "10",
        retrievalReferenceNumber = "1234567890"
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
                iccData = "iccData",
                maskedPan = "xxxxxxxx4020",
                bank = "SANTANDER",
                type = "CREDIT",
                brand = "VISA",
                workingKey = null,
                nationality = null
            ),
            inputMode = MANUAL,
            previousTransactionInputMode = "MANUAL"
        ),
        amount = AcquirerResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(
                AcquirerResponse.Amount.Breakdown(
                    description = "description",
                    amount = "1000"
                )
            )
        ),
        datetime = aDatetime(),
        trace = "123",
        ticket = "234",
        terminal = AcquirerResponse.Terminal(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
            serialCode = "1234",
            softwareVersion = "2.0",
            features = listOf("CHIP")
        ),
        merchant = AcquirerResponse.Merchant(
            id = "0f14d0ab-9605-4a62-a9e4-5ed26688389b"
        ),
        batch = "111",
        installments = "10",
        authorization = AcquirerResponse.Authorization(
            code = "4002",
            status = AcquirerResponse.Status(
                code = "APPROVED",
                situation = AcquirerResponse.Status.Situation(
                    id = "id",
                    description = "description"
                )
            ),
            retrievalReferenceNumber = "1234567890"
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
        features = listOf(CHIP)
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

fun aOperationEntity(operationType: OperationType) =
    OperationEntity(
        id = aOperationId,
        acquirerId = acquirerId,
        originalOperationId = aOperationId,
        type = operationType.name,
        authorizationCode = anAuthorization().authorizationCode,
        displayMessage = anAuthorization().displayMessage,
        statusCode = anAuthorization().status.code,
        situationCode = anAuthorization().status.situation?.id,
        situationMessage = anAuthorization().status.situation?.description,
        merchantId = aMerchantId,
        terminalId = aTerminalId,
        customerId = aCustomerId,
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

fun aCard() = Payment.Capture.Card(
    holder = Payment.Capture.Card.Holder(
        name = holderName,
        identification = Payment.Capture.Card.Holder.Identification(
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

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = aDatetime(),
        errors = listOf(
            ApiError(
                code = 401,
                resource = "/reimbursements",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

fun aDatetime() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)

fun <T> aConsumerMessage(message: T) =
    ConsumerMessage(
        message = message,
        key = "a key",
        topic = "a topic",
        partitionId = null
    )
