package com.menta.api.merchants

import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantRequest
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.adapter.`in`.model.MerchantRequest
import com.menta.api.merchants.adapter.`in`.model.MerchantResponse
import com.menta.api.merchants.adapter.`in`.model.UpdateRequest
import com.menta.api.merchants.domain.Country.ARG
import com.menta.api.merchants.domain.Country.COL
import com.menta.api.merchants.domain.LegalType.LEGAL_ENTITY
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.Merchant.BusinessOwner
import com.menta.api.merchants.domain.Merchant.Representative
import com.menta.api.merchants.domain.Merchant.Representative.RepresentativeId
import com.menta.api.merchants.domain.Merchant.SettlementCondition
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.domain.PreMerchant
import com.menta.api.merchants.domain.State.BUENOS_AIRES
import com.menta.api.merchants.domain.State.CORDOBA
import com.menta.api.merchants.domain.Status
import com.menta.api.merchants.shared.error.model.ApiErrorResponse
import com.menta.api.merchants.shared.error.model.ApiErrorResponse.ApiError
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

fun aMerchant() =
    Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = Representative(
            representativeId = RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = Merchant.Address(
            state = BUENOS_AIRES,
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
        status = Status.ACTIVE,
        tax = Merchant.Tax("id", "a type"),
        settlementCondition = SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        createDate = datetime,
        updateDate = datetime,
        deleteDate = null
    )

fun aDeletedMerchant() =
    Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = Representative(
            representativeId = RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = Merchant.Address(
            state = BUENOS_AIRES,
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
        status = Status.ACTIVE,
        tax = Merchant.Tax("id", "a type"),
        settlementCondition = SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        createDate = datetime,
        updateDate = datetime,
        deleteDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00")
    )

fun aPreMerchant() =
    PreMerchant(
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = PreMerchant.Representative(
            representativeId = PreMerchant.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = PreMerchant.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = PreMerchant.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = PreMerchant.Address(
            state = BUENOS_AIRES,
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
        tax = PreMerchant.Tax("id", "a type"),
        settlementCondition = PreMerchant.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        )
    )

fun aMerchantCreated() =
    Merchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = Representative(
            representativeId = RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = Merchant.Address(
            state = BUENOS_AIRES,
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
        status = Status.ACTIVE,
        tax = Merchant.Tax("id", "a type"),
        settlementCondition = SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        createDate = datetime,
        updateDate = datetime,
        deleteDate = null
    )

val anApiErrorResponse =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiError(
                code = "a code",
                resource = "a resource",
                message = "a message",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )

fun aMerchantRequest() =
    MerchantRequest(
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = MerchantRequest.Representative(
            representativeId = MerchantRequest.Representative.RepresentativeId(
                type = "id",
                number = "12313"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jorge",
            surname = "Perez"
        ),
        businessOwner = MerchantRequest.BusinessOwner(
            "Jorge",
            "Perez",
            OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            MerchantRequest.BusinessOwner.OwnerId("a type", "4564")
        ),
        merchantCode = "a code",
        address = MerchantRequest.Address(BUENOS_AIRES, "a city", "a zip", "a street", "a number", "", ""),
        email = "email@gmail.com",
        phone = "112345678900",
        activity = "a activity",
        category = "7372",
        tax = MerchantRequest.Tax("id", "a type"),
        settlementCondition = MerchantRequest.SettlementCondition("a fee", "a settlement", "342434434242")
    )

fun aUpdateRequest() =
    UpdateRequest(
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "name",
        fantasyName = "name",
        representative = UpdateRequest.Representative(
            representativeId = UpdateRequest.Representative.RepresentativeId(
                type = "CUIL",
                number = "99999999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Pedro",
            surname = "Lopez"
        ),
        businessOwner = UpdateRequest.BusinessOwner(
            "Jorge",
            "Perez",
            OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            UpdateRequest.BusinessOwner.OwnerId("a type", "4564")
        ),
        merchantCode = "a new code",
        address = UpdateRequest.Address(CORDOBA, "Cordoba capital", "11111", "Av. Cordoba", "11111", null, null),
        email = "john@doe.com",
        phone = "88888888",
        activity = "the activity",
        category = "category",
        tax = UpdateRequest.Tax("99999999999", "MONOTRIBUTISTA"),
        settlementCondition = UpdateRequest.SettlementCondition("a new fee", "a new settlement", "99999999999")
    )

fun aMerchantResponse() =
    MerchantResponse(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
        country = ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        representative = MerchantResponse.Representative(
            representativeId = MerchantResponse.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        merchantCode = "123456",
        businessOwner = MerchantResponse.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = MerchantResponse.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        address = MerchantResponse.Address(
            state = "BUENOS_AIRES",
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
        tax = MerchantResponse.Tax("id", "a type"),
        settlementCondition = MerchantResponse.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        createDate = datetime,
        updateDate = datetime,
        deleteDate = null
    )

fun aMerchantQuery(merchantId: UUID? = null, status: Status? = null, customerId: UUID? = null, createDate: String? = null) =
    MerchantQuery(merchantId = merchantId, status = status, customerId = customerId, createDate = createDate)

fun anAcquirerMerchantRequest() =
    AcquirerMerchantRequest(
        acquirerId = "GPS",
        code = "an acquirer merchant id"
    )

fun aPreAcquirerMerchant() =
    PreAcquirerMerchant(
        merchantId = aMerchantId,
        acquirerId = "GPS",
        code = "an acquirer merchant id"
    )
