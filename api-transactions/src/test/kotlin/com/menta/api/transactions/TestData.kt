package com.menta.api.transactions

import com.menta.api.transactions.TestConstants.Companion.AMOUNT
import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_CODE
import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_MESSAGE
import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_RRN
import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_STATUS_SITUATION_DESCRIPTION
import com.menta.api.transactions.TestConstants.Companion.AUTHORIZATION_STATUS_SITUATION_ID
import com.menta.api.transactions.TestConstants.Companion.CARD_BANK
import com.menta.api.transactions.TestConstants.Companion.CARD_BRAND
import com.menta.api.transactions.TestConstants.Companion.CARD_MASK
import com.menta.api.transactions.TestConstants.Companion.CARD_TYPE
import com.menta.api.transactions.TestConstants.Companion.CARD_TYPE_DEBIT
import com.menta.api.transactions.TestConstants.Companion.CURRENCY
import com.menta.api.transactions.TestConstants.Companion.CUSTOMER_ID
import com.menta.api.transactions.TestConstants.Companion.HOLDER_DOCUMENT
import com.menta.api.transactions.TestConstants.Companion.HOLDER_NAME
import com.menta.api.transactions.TestConstants.Companion.INSTALLMENTS_NUMBER
import com.menta.api.transactions.TestConstants.Companion.INSTALLMENTS_PLAN
import com.menta.api.transactions.TestConstants.Companion.MERCHANT_ID
import com.menta.api.transactions.TestConstants.Companion.OPERATION_ID
import com.menta.api.transactions.TestConstants.Companion.OPERATION_TYPE
import com.menta.api.transactions.TestConstants.Companion.PAYMENT_ID
import com.menta.api.transactions.TestConstants.Companion.REFUNDED_AMOUNT
import com.menta.api.transactions.TestConstants.Companion.SERIAL_CODE
import com.menta.api.transactions.TestConstants.Companion.TERMINAL_ID
import com.menta.api.transactions.TestConstants.Companion.TICKET_ID
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_TYPE
import com.menta.api.transactions.TestConstants.Companion.aDatetime
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedAnnulment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedBillPayment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedPayment
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedRefund
import com.menta.api.transactions.adapter.`in`.model.OperationResponse
import com.menta.api.transactions.adapter.`in`.model.TransactionResponse
import com.menta.api.transactions.domain.Authorization
import com.menta.api.transactions.domain.Card
import com.menta.api.transactions.domain.Card.Holder
import com.menta.api.transactions.domain.CardType
import com.menta.api.transactions.domain.Customer
import com.menta.api.transactions.domain.Feature.CHIP
import com.menta.api.transactions.domain.InputMode
import com.menta.api.transactions.domain.Installments
import com.menta.api.transactions.domain.Operation
import com.menta.api.transactions.domain.OperationType.PAYMENT
import com.menta.api.transactions.domain.Payment
import com.menta.api.transactions.domain.ReversalOperation
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.StatusCode.APPROVED
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.Transaction.Terminal
import com.menta.api.transactions.domain.TransactionType.ACQUIRER
import com.menta.api.transactions.domain.TransactionType.BILL
import com.menta.api.transactions.shared.error.model.ApiErrorResponse
import java.time.OffsetDateTime
import java.util.UUID
import com.menta.api.transactions.adapter.out.db.entity.Operation as OperationEntity
import com.menta.api.transactions.adapter.out.db.entity.Transaction as TransactionEntity

fun aCreatedPaymentMessage() =
    CreatedPayment(
        id = OPERATION_ID,
        ticketId = TICKET_ID,
        origin = ACQUIRER,
        authorization = Authorization(
            authorizationCode = AUTHORIZATION_CODE,
            status = Authorization.Status(
                code = APPROVED,
                situation = Authorization.Status.Situation(
                    id = AUTHORIZATION_STATUS_SITUATION_ID,
                    description = AUTHORIZATION_STATUS_SITUATION_DESCRIPTION
                )
            ),
            retrievalReferenceNumber = AUTHORIZATION_RRN,
            displayMessage = AUTHORIZATION_MESSAGE
        ),
        data = Payment(
            amount = Payment.Amount(
                total = AMOUNT,
                currency = "ARS",
                breakdown = listOf(
                    Payment.Amount.Breakdown(
                        description = "descripcion",
                        amount = "1000",
                    )
                )
            ),
            installments = INSTALLMENTS_NUMBER,
            trace = "123",
            ticket = "234",
            batch = "111",
            merchant = aMerchant(),
            terminal = aTerminal(),
            customer = aPaymentCustomer(),
            capture = Payment.Capture(
                card = Payment.Capture.Card(
                    holder = Payment.Capture.Card.Holder(
                        name = HOLDER_NAME,
                        identification = Payment.Capture.Card.Holder.Identification(
                            number = HOLDER_DOCUMENT,
                            type = "DNI"
                        )
                    ),
                    pan = CARD_MASK,
                    expirationDate = "0622",
                    cvv = "234",
                    track1 = "track 1",
                    track2 = "track2",
                    iccData = "data",
                    cardSequenceNumber = "card sequence",
                    bank = "SANTANDER",
                    type = CARD_TYPE,
                    brand = "VISA",
                    pin = "000",
                    ksn = "456"
                ),
                inputMode = "MANUAL",
                previousTransactionInputMode = null
            ),
            datetime = aDatetime(),
        )
    )

fun aCreatedAnnulment() =
    CreatedAnnulment(
        id = OPERATION_ID,
        ticketId = TICKET_ID,
        authorization = CreatedAnnulment.Authorization(
            authorizationCode = AUTHORIZATION_CODE,
            status = CreatedAnnulment.Authorization.Status(
                code = APPROVED,
                situation = CreatedAnnulment.Authorization.Status.Situation(
                    id = AUTHORIZATION_STATUS_SITUATION_ID,
                    description = AUTHORIZATION_STATUS_SITUATION_DESCRIPTION
                )
            ),
            retrievalReferenceNumber = AUTHORIZATION_RRN,
            displayMessage = null
        ),
        data = CreatedAnnulment.Annulments(
            paymentId = PAYMENT_ID,
            amount = CreatedAnnulment.Annulments.Amount(
                total = REFUNDED_AMOUNT,
                currency = "ARS",
                breakdown = listOf(
                    CreatedAnnulment.Annulments.Amount.Breakdown(
                        description = "descripcion",
                        amount = "1000",
                    )
                )
            ),
            installments = INSTALLMENTS_NUMBER,
            trace = "123",
            ticket = "234",
            batch = "111",
            merchant = aMerchantAnnulment(),
            terminal = aTerminalAnnulment(),
            customer = aCustomerAnnulment(),
            capture = CreatedAnnulment.Annulments.Capture(
                card = CreatedAnnulment.Annulments.Capture.Card(
                    holder = CreatedAnnulment.Annulments.Capture.Card.Holder(
                        name = HOLDER_NAME,
                        identification = CreatedAnnulment.Annulments.Capture.Card.Holder.Identification(
                            number = HOLDER_DOCUMENT,
                            type = "DNI"
                        )
                    ),
                    pan = CARD_MASK,
                    expirationDate = "0622",
                    cvv = "234",
                    track1 = "track 1",
                    track2 = "track2",
                    iccData = "data",
                    cardSequenceNumber = "card sequence",
                    bank = "SANTANDER",
                    type = CARD_TYPE,
                    brand = "VISA",
                    pin = "000",
                    ksn = "456"
                ),
                inputMode = "MANUAL",
                previousTransactionInputMode = null
            ),
            datetime = aDatetime(),
        )
    )

fun aCreatedRefund(status: StatusCode = APPROVED) =
    CreatedRefund(
        id = OPERATION_ID,
        ticketId = TICKET_ID,
        authorization = CreatedRefund.Authorization(
            authorizationCode = AUTHORIZATION_CODE,
            status = CreatedRefund.Authorization.Status(
                code = status,
                situation = CreatedRefund.Authorization.Status.Situation(
                    id = AUTHORIZATION_STATUS_SITUATION_ID,
                    description = AUTHORIZATION_STATUS_SITUATION_DESCRIPTION
                )
            ),
            retrievalReferenceNumber = AUTHORIZATION_RRN,
            displayMessage = null
        ),
        data = CreatedRefund.Refund(
            paymentId = PAYMENT_ID,
            amount = CreatedRefund.Refund.Amount(
                total = REFUNDED_AMOUNT,
                currency = "ARS",
                breakdown = listOf(
                    CreatedRefund.Refund.Amount.Breakdown(
                        description = "descripcion",
                        amount = REFUNDED_AMOUNT,
                    )
                )
            ),
            installments = INSTALLMENTS_NUMBER,
            trace = "123",
            ticket = "234",
            batch = "111",
            merchant = aMerchantRefund(),
            terminal = aTerminalRefund(),
            customer = aCustomerRefund(),
            capture = CreatedRefund.Refund.Capture(
                card = CreatedRefund.Refund.Capture.Card(
                    holder = CreatedRefund.Refund.Capture.Card.Holder(
                        name = HOLDER_NAME,
                        identification = CreatedRefund.Refund.Capture.Card.Holder.Identification(
                            number = HOLDER_DOCUMENT,
                            type = "DNI"
                        )
                    ),
                    pan = CARD_MASK,
                    expirationDate = "0622",
                    cvv = "234",
                    track1 = "track 1",
                    track2 = "track2",
                    iccData = "data",
                    cardSequenceNumber = "card sequence",
                    bank = "SANTANDER",
                    type = CARD_TYPE,
                    brand = "VISA",
                    pin = "000",
                    ksn = "456"
                ),
                inputMode = "MANUAL",
                previousTransactionInputMode = null
            ),
            datetime = aDatetime(),
        )
    )

fun aTransaction(transactionId: UUID, refundedAmount: String? = null) =
    Transaction(
        id = transactionId,
        type = ACQUIRER,
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        terminal = Terminal(
            id = UUID.fromString(TERMINAL_ID),
            serialCode = SERIAL_CODE
        ),
        currency = CURRENCY,
        amount = AMOUNT,
        refundedAmount = refundedAmount,
        operation = Operation(
            id = UUID.fromString(OPERATION_ID),
            ticketId = TICKET_ID,
            datetime = aDatetime(),
            type = PAYMENT,
            status = APPROVED,
            amount = AMOUNT,
            acquirerId = AUTHORIZATION_RRN
        ),
        installment = Installments(
            number = INSTALLMENTS_NUMBER,
            plan = INSTALLMENTS_PLAN
        ),
        card = Card(
            type = CardType.valueOf(CARD_TYPE),
            mask = CARD_MASK,
            brand = CARD_BRAND,
            bank = CARD_BANK,
            holder = Holder(
                name = HOLDER_NAME,
                document = HOLDER_DOCUMENT
            )
        )
    )

fun aTransactionBillDebit(transactionId: UUID, refundedAmount: String? = null) =
    Transaction(
        id = transactionId,
        type = BILL,
        merchantId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        customerId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        terminal = Terminal(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            serialCode = "123456789"
        ),
        currency = CURRENCY,
        amount = "1",
        refundedAmount = refundedAmount,
        operation = Operation(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            ticketId = TICKET_ID,
            datetime = aDatetime(),
            type = PAYMENT,
            status = APPROVED,
            amount = "1",
            acquirerId = null
        ),
        installment = Installments(
            number = "1",
            plan = INSTALLMENTS_PLAN
        ),
        card = Card(
            type = CardType.valueOf(CARD_TYPE_DEBIT),
            mask = null,
            brand = null,
            bank = null,
            holder = Holder(
                name = null,
                document = null
            )
        )
    )

fun aTransactionBillCash(transactionId: UUID, refundedAmount: String? = null) =
    Transaction(
        id = transactionId,
        type = BILL,
        merchantId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        customerId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        terminal = Terminal(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            serialCode = "123456789"
        ),
        currency = CURRENCY,
        amount = "1",
        refundedAmount = refundedAmount,
        operation = Operation(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            ticketId = TICKET_ID,
            datetime = aDatetime(),
            type = PAYMENT,
            status = APPROVED,
            amount = "1",
            acquirerId = null
        ),
        installment = Installments(
            number = "1",
            plan = INSTALLMENTS_PLAN
        ),
        card = Card(
            type = null,
            mask = null,
            brand = null,
            bank = null,
            holder = Holder(
                name = null,
                document = null
            )
        )
    )

fun aTransactionEntity() =
    TransactionEntity(
        transactionId = UUID.fromString(TRANSACTION_ID),
        type = TRANSACTION_TYPE,
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        terminalId = UUID.fromString(TERMINAL_ID),
        originalAmount = AMOUNT,
        refundedAmount = null,
        currency = CURRENCY,
        installmentsNumber = INSTALLMENTS_NUMBER,
        installmentsPlan = INSTALLMENTS_PLAN,
        cardType = CARD_TYPE,
        cardMask = CARD_MASK,
        cardBank = CARD_BANK,
        cardBrand = CARD_BRAND,
        holderDocument = HOLDER_DOCUMENT,
        holderName = HOLDER_NAME,
        createdDatetime = aDatetime(),
        serialCode = SERIAL_CODE
    )

fun aOperationEntity() =
    OperationEntity(
        id = UUID.fromString(OPERATION_ID),
        ticketId = TICKET_ID,
        type = OPERATION_TYPE,
        status = APPROVED,
        datetime = aDatetime(),
        transaction = aTransactionEntity(),
        acquirerId = AUTHORIZATION_RRN
    )

fun aMerchant() =
    Payment.Merchant(
        id = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
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

fun aReversalMerchant() =
    ReversalOperation.Merchant(
        id = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
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

fun aPaymentCustomer() =
    Payment.Customer(
        id = UUID.fromString(CUSTOMER_ID),
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

fun aTerminal() =
    Payment.Terminal(
        id = UUID.fromString(TERMINAL_ID),
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        serialCode = SERIAL_CODE,
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf("CHIP")
    )

fun aMerchantAnnulment() =
    CreatedAnnulment.Annulments.Merchant(
        id = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = CreatedAnnulment.Annulments.Merchant.Representative(
            id = CreatedAnnulment.Annulments.Merchant.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = CreatedAnnulment.Annulments.Merchant.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            id = CreatedAnnulment.Annulments.Merchant.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = CreatedAnnulment.Annulments.Merchant.Address(
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
        category = "7375",
        tax = CreatedAnnulment.Annulments.Merchant.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = CreatedAnnulment.Annulments.Merchant.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aCustomerAnnulment() =
    CreatedAnnulment.Annulments.Customer(
        id = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = CreatedAnnulment.Annulments.Customer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = CreatedAnnulment.Annulments.Customer.Address(
            "a state",
            "a city",
            "a zip",
            "a street",
            "a number",
            "",
            ""
        ),
        representative = CreatedAnnulment.Annulments.Customer.Representative(
            representativeId = CreatedAnnulment.Annulments.Customer.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = CreatedAnnulment.Annulments.Customer.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = CreatedAnnulment.Annulments.Customer.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = CreatedAnnulment.Annulments.Customer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "ACTIVE"
    )

fun aTerminalRefund() =
    CreatedRefund.Refund.Terminal(
        id = UUID.fromString(TERMINAL_ID),
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        serialCode = AUTHORIZATION_RRN,
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf("CHIP")
    )

fun aMerchantRefund() =
    CreatedRefund.Refund.Merchant(
        id = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
        legalType = "legal type",
        businessName = "business name",
        fantasyName = "fantasy name",
        representative = CreatedRefund.Refund.Merchant.Representative(
            id = CreatedRefund.Refund.Merchant.Representative.RepresentativeId(
                type = "type",
                number = "number"
            ),
            birthDate = "2022-03-31T22:01:01.999+07:00",
            name = "name",
            surname = "surname"
        ),
        businessOwner = CreatedRefund.Refund.Merchant.BusinessOwner(
            name = "name",
            surname = "surname",
            birthDate = "birth date",
            id = CreatedRefund.Refund.Merchant.BusinessOwner.OwnerId(
                type = "type",
                number = "number"
            )
        ),
        merchantCode = "merchant code",
        address = CreatedRefund.Refund.Merchant.Address(
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
        category = "7375",
        tax = CreatedRefund.Refund.Merchant.Tax(
            id = "id",
            type = "a type"
        ),
        settlementCondition = CreatedRefund.Refund.Merchant.SettlementCondition(
            transactionFee = "0",
            settlement = "settlement",
            cbuOrCvu = "cbu 111111111"
        )
    )

fun aReversalOperation() =
    ReversalOperation(
        operationId = UUID.fromString(OPERATION_ID),
        acquirerId = "123456789",
        capture = ReversalOperation.Capture(
            card = ReversalOperation.Capture.Card(
                holder = ReversalOperation.Capture.Card.Holder(
                    name = HOLDER_NAME,
                    identification = ReversalOperation.Capture.Card.Holder.Identification(
                        number = HOLDER_DOCUMENT,
                        type = "DNI"
                    )
                ),
                pan = CARD_MASK,
                expirationDate = "0622",
                cvv = "234",
                track1 = "track 1",
                track2 = "track2",
                iccData = "data",
                cardSequenceNumber = "card sequence",
                bank = "SANTANDER",
                type = CARD_TYPE,
                brand = "VISA",
                pin = "000",
                ksn = "456"
            ),
            inputMode = InputMode.CONTACTLESS,
            previousTransactionInputMode = null
        ),
        amount = ReversalOperation.Amount(
            total = AMOUNT,
            currency = "ARS",
            breakdown = listOf(
                ReversalOperation.Amount.Breakdown(
                    description = "descripcion",
                    amount = "1000",
                )
            )
        ),
        installments = INSTALLMENTS_NUMBER,
        trace = "123",
        ticket = "234",
        batch = "111",
        terminal = ReversalOperation.Terminal(
            id = UUID.fromString(TERMINAL_ID),
            merchantId = UUID.fromString(MERCHANT_ID),
            customerId = UUID.fromString(CUSTOMER_ID),
            serialCode = AUTHORIZATION_RRN,
            hardwareVersion = "2.0",
            softwareVersion = "2.0",
            tradeMark = "1234",
            model = "1234",
            status = "ACTIVE",
            features = listOf(CHIP)
        ),
        merchant = aReversalMerchant(),
        datetime = aDatetime(),
        customer = aCustomer()
    )

fun aCustomerRefund() =
    CreatedRefund.Refund.Customer(
        id = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = CreatedRefund.Refund.Customer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = CreatedRefund.Refund.Customer.Address(
            "a state",
            "a city",
            "a zip",
            "a street",
            "a number",
            "",
            ""
        ),
        representative = CreatedRefund.Refund.Customer.Representative(
            representativeId = CreatedRefund.Refund.Customer.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = CreatedRefund.Refund.Customer.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = CreatedRefund.Refund.Customer.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = CreatedRefund.Refund.Customer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "ACTIVE"
    )

fun aCustomer() =
    Customer(
        id = UUID.fromString(CUSTOMER_ID),
        country = "ARG",
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = Customer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = Customer.Address(
            "a state",
            "a city",
            "a zip",
            "a street",
            "a number",
            "",
            ""
        ),
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

fun aTerminalAnnulment() =
    CreatedAnnulment.Annulments.Terminal(
        id = UUID.fromString(TERMINAL_ID),
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        serialCode = AUTHORIZATION_RRN,
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf("CHIP")
    )

fun aCreatedBillPaymentMessageCash() =
    CreatedBillPayment(
        billPaymentId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        merchantId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        customerId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        terminalId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        serialCode = "123456789",
        currency = "ARS",
        operation = CreatedBillPayment.Operation(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            datetime = aDatetime(),
            amount = "1",
            ticketId = TICKET_ID
        ),
        installmentNumber = "1",
        card = null
    )

fun aCreatedBillPaymentMessageDebit() =
    CreatedBillPayment(
        billPaymentId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        merchantId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        customerId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        terminalId = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
        serialCode = "123456789",
        currency = "ARS",
        operation = CreatedBillPayment.Operation(
            id = UUID.fromString("4d36de9d-53f9-4025-9d58-6c35adb8e106"),
            datetime = aDatetime(),
            amount = "1",
            ticketId = TICKET_ID
        ),
        installmentNumber = "1",
        card = CreatedBillPayment.Card(
            type = "DEBIT",
            mask = null,
            brand = null,
            bank = null,
            holder = null
        )
    )

fun aTransactionResponse() =
    TransactionResponse(
        id = UUID.fromString(TRANSACTION_ID),
        type = ACQUIRER,
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        terminal = TransactionResponse.Terminal(
            id = UUID.fromString(TERMINAL_ID),
            serialCode = SERIAL_CODE
        ),
        currency = CURRENCY,
        amount = AMOUNT,
        refundedAmount = null,
        operation = aOperationResponse(),
        installment = Installments(
            number = INSTALLMENTS_NUMBER,
            plan = INSTALLMENTS_PLAN
        ),
        card = TransactionResponse.Card(
            type = CardType.valueOf(CARD_TYPE),
            maskedPan = CARD_MASK,
            brand = CARD_BRAND,
            bank = CARD_BANK,
            holder = TransactionResponse.Card.Holder(
                name = HOLDER_NAME,
                document = HOLDER_DOCUMENT
            )
        )
    )

fun aOperationResponse() =
    OperationResponse(
        id = UUID.fromString(OPERATION_ID),
        ticketId = TICKET_ID,
        datetime = aDatetime(),
        type = PAYMENT,
        amount = AMOUNT,
        acquirerId = AUTHORIZATION_RRN,
        status = APPROVED
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiErrorResponse.ApiError(
                code = "a code",
                resource = "a resource",
                message = "a message",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )
