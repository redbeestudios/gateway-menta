package com.menta.bff.devices.login.entities.merchant

import com.menta.bff.devices.login.entities.customer.customerId
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.entities.merchant.domain.taxes.FiscalCondition.RESPONSABLE_INSCRIPTO
import com.menta.bff.devices.login.entities.merchant.domain.taxes.PaymentMethod.CREDIT
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant.FeeRule
import com.menta.bff.devices.login.shared.domain.Country.ARG
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

val merchantId = UUID.fromString("93944b4a-1100-438d-b0a9-b0afcb44ac94")
val taxID = UUID.fromString("93944b4a-1100-438d-b0a9-b0afcb44ac94")

fun aMerchant() =
    Merchant(
        id = merchantId,
        customerId = customerId,
        country = "ARG",
        legalType = "LEGAL_ENTITY",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = Merchant.Representative(
            representativeId = Merchant.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = Merchant.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = Merchant.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = Merchant.Address(
            state = "a state",
            city = "a city",
            zip = "a zip",
            street = "a street",
            number = "a number",
            floor = null,
            apartment = null
        ),
        email = "hola@hola.com",
        phone = "1234567890",
        activity = "activity",
        category = "7372",
        taxCondition = RESPONSABLE_INSCRIPTO,
        tax = Merchant.Tax("id", "a type"),
        settlementCondition = Merchant.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        deleteDate = null
    )

fun aTaxMerchant() =
    TaxMerchant(
        id = taxID,
        merchantId = merchantId,
        customerId = customerId,
        country = ARG,
        taxCondition = RESPONSABLE_INSCRIPTO,
        feeRules = listOf(
            FeeRule(
                paymentMethod = CREDIT,
                term = 6,
                installments = 6,
                commission = BigDecimal.ONE,
                mentaCommission = BigDecimal.ONE,
                discount = BigDecimal.ONE
            )
        )
    )
