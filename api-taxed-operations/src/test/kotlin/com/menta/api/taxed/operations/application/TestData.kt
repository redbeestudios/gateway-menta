package com.menta.api.taxed.operations.application

import com.menta.api.taxed.operations.adapter.out.db.entity.Tax
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxCustomer
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxMerchant
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import com.menta.api.taxed.operations.domain.Country
import com.menta.api.taxed.operations.domain.CreatedPayment
import com.menta.api.taxed.operations.domain.Feature
import com.menta.api.taxed.operations.domain.FeeRule
import com.menta.api.taxed.operations.domain.FiscalCondition
import com.menta.api.taxed.operations.domain.InputMode
import com.menta.api.taxed.operations.domain.Payment
import com.menta.api.taxed.operations.domain.PaymentMethod
import com.menta.api.taxed.operations.domain.PaymentTaxCalculation
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import com.menta.api.taxed.operations.domain.TaxCalculation
import com.menta.api.taxed.operations.domain.TaxDispersion
import com.menta.api.taxed.operations.domain.TaxRule
import com.menta.api.taxed.operations.domain.TaxRules
import com.menta.api.taxed.operations.domain.TaxType
import com.menta.api.taxed.operations.shared.util.round2Decimal
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

const val cardPan = "333344445555"
val aPaymentId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aMerchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aCustomerId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aTerminalId: UUID = UUID.fromString("72171704-7806-4347-b08b-bc2d2d96e68e")
val aTaxId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val aTaxCustomerId: Int = 1
val aTaxMerchantId: Int = 1

fun aPaymentTaxCalculation() =
    PaymentTaxCalculation(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b").toString(),
        createdPayment = aCreatedPayment(),
        taxCalculation = aTaxCalculation(),
        taxes = aTaxRules()
    )

fun aTaxRules() =
    TaxRules(
        merchantTaxRules = listOf(aTaxRule(0.03.toBigDecimal(), TaxType.IVA), aTaxRule(0.01.toBigDecimal(), TaxType.GANANCIAS)),
        customerTaxRules = listOf(aTaxRule(0.03.toBigDecimal(), TaxType.IVA), aTaxRule(0.01.toBigDecimal(), TaxType.GANANCIAS))
    )

fun aTaxCalculation() =
    TaxCalculation(
        grossAmount = 10000.toBigDecimal(),
        merchantTaxDispersion = aTaxDispersion(),
        customerTaxDispersion = aTaxDispersion()
    )
fun aPaymentTaxedOperation() =
    PaymentTaxedOperation(
        paymentId = aPaymentId,
        terminalId = aTerminalId,
        merchantId = aMerchantId,
        customerId = aCustomerId,
        grossAmount = "10000",
        currency = "ARS",
        installments = "10",
        merchantDispersion = aTaxDispersion(),
        customerDispersion = aTaxDispersion(),
        taxes = aTaxRules()
    )

fun aTaxedOperation() =
    TaxedOperation(
        id = Int.MIN_VALUE,
        paymentId = aPaymentId,
        terminalId = aTerminalId,
        merchantId = aMerchantId,
        customerId = aCustomerId,
        grossAmount = "10000",
        currency = "ARS",
        installments = "10",
        taxCustomer = aTaxCustomer(),
        taxMerchant = aTaxMerchant(),
        merchantTaxRules = setOf(aTax(0.05.toBigDecimal(), TaxType.IVA), aTax(0.06.toBigDecimal(), TaxType.GANANCIAS)),
        customerTaxRules = setOf(aTax(0.05.toBigDecimal(), TaxType.IVA), aTax(0.06.toBigDecimal(), TaxType.GANANCIAS)),
        createDatetime = OffsetDateTime.now()
    )

fun aTaxDispersion() =
    TaxDispersion(
        taxedAmount = BigDecimal(86.71).round2Decimal(),
        iva = BigDecimal(2.71).round2Decimal(),
        ganancias = BigDecimal(0.90).round2Decimal(),
        grossCommission = BigDecimal(0.08).round2Decimal(),
        grossCommissionWithTax = BigDecimal(0.08).round2Decimal(),
        partialGrossAmount = BigDecimal(0.08).round2Decimal(),
        ivaCommission = BigDecimal(0.08).round2Decimal(),
        feeRule = aFeeRule(),
        nextPaymentDate = aDate()
    )

fun aTaxCustomer() =
    TaxCustomer(
        id = aTaxCustomerId,
        taxedAmount = BigDecimal(86.71).round2Decimal(),
        iva = BigDecimal(2.71).round2Decimal(),
        ganancias = BigDecimal(0.90).round2Decimal(),
        commission = BigDecimal(0.08).round2Decimal(),
        grossCommission = BigDecimal(0.08).round2Decimal(),
        grossCommissionWithTax = BigDecimal(0.08).round2Decimal(),
        partialGrossAmount = BigDecimal(0.08).round2Decimal(),
        ivaCommission = BigDecimal(0.08).round2Decimal(),
        paymentMethod = PaymentMethod.CREDIT,
        term = 40,
        installments = 3,
        discount = BigDecimal.ZERO,
        nextPaymentDate = aDate(),
        taxedOperation = null
    )

fun aTaxMerchant() =
    TaxMerchant(
        id = aTaxMerchantId,
        taxedAmount = BigDecimal(86.71).round2Decimal(),
        iva = BigDecimal(2.71).round2Decimal(),
        ganancias = BigDecimal(0.90).round2Decimal(),
        commission = BigDecimal(0.08).round2Decimal(),
        grossCommission = BigDecimal(0.08).round2Decimal(),
        grossCommissionWithTax = BigDecimal(0.08).round2Decimal(),
        partialGrossAmount = BigDecimal(0.08).round2Decimal(),
        ivaCommission = BigDecimal(0.08).round2Decimal(),
        paymentMethod = PaymentMethod.CREDIT,
        term = 40,
        installments = 3,
        discount = BigDecimal.ZERO,
        nextPaymentDate = aDate(),
        taxedOperation = null
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
            inputMode = InputMode.MANUAL,
            previousTransactionInputMode = null
        ),
        datetime = aDate(),
    )

fun aTerminal() =
    Payment.Terminal(
        id = aTerminalId,
        merchantId = aMerchantId,
        customerId = aCustomerId,
        serialCode = "1234",
        hardwareVersion = "2.0",
        softwareVersion = "2.0",
        tradeMark = "1234",
        model = "1234",
        status = "ACTIVE",
        features = listOf(Feature.CHIP)
    )

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

fun aMerchant() =
    Payment.Merchant(
        id = aMerchantId,
        customerId = aCustomerId,
        country = Payment.Merchant.Country.ARG,
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
        category = "a category",
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

fun aCreatedPayment() =
    CreatedPayment(
        id = aPaymentId.toString(),
        data = aPayment()
    )

fun aTaxRule(percentage: BigDecimal, taxType: TaxType) = TaxRule(
    id = aTaxId,
    name = "tax1",
    description = "",
    percentage = percentage,
    active = true,
    fiscalCondition = FiscalCondition.RESPONSABLE_INSCRIPTO,
    country = Country.ARG,
    paymentMethod = PaymentMethod.CREDIT,
    taxType = taxType,
    taxFree = true
)

fun aTax(percentage: BigDecimal, taxType: TaxType) =
    Tax(
        id = aTaxId,
        name = "tax1",
        description = "",
        percentage = percentage,
        fiscalCondition = FiscalCondition.RESPONSABLE_INSCRIPTO,
        country = Country.ARG,
        paymentMethod = PaymentMethod.CREDIT,
        taxType = taxType
    )

fun aFeeRule(paymentMethod: PaymentMethod? = PaymentMethod.CREDIT, installments: Int = 3) =
    FeeRule(
        paymentMethod = paymentMethod!!,
        term = 40,
        installments = installments,
        commission = 0.08.toBigDecimal(),
        discount = BigDecimal.ZERO
    )
fun aDate() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
