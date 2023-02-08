package com.menta.api.customers

import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerRequest
import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerResponse
import com.menta.api.customers.acquirer.domain.Acquirer
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.customer.adapter.`in`.model.CustomerRequest
import com.menta.api.customers.customer.adapter.`in`.model.CustomerResponse
import com.menta.api.customers.customer.adapter.`in`.model.UpdateRequest
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.LegalType.LEGAL_ENTITY
import com.menta.api.customers.customer.domain.PreCustomer
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.shared.error.model.ApiErrorResponse
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

val aCustomerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val anAcquirerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c")

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

fun anAcquirerCustomer() =
    AcquirerCustomer(
        id = anAcquirerId,
        customerId = aCustomerId,
        acquirerId = "GPS",
        code = "an acquirer customer id",
        createDate = datetime,
        updateDate = datetime
    )

fun aPreAcquirerCustomer() =
    PreAcquirerCustomer(
        customerId = aCustomerId,
        acquirerId = "GPS",
        code = "an acquirer customer id"
    )

fun anAcquirer() =
    Acquirer(
        id = "GPS",
    )

val aCustomerCreated =
    Customer(
        id = aCustomerId,
        country = Country.ARG,
        legalType = LEGAL_ENTITY,
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
        status = Status.ACTIVE,
        deleteDate = null,
        createDate = datetime,
        updateDate = datetime
    )

fun aCustomerDeleted() = aCustomerCreated.copy(deleteDate = OffsetDateTime.now())

fun anAcquirerCustomerResponse() =
    AcquirerCustomerResponse(
        customerId = aCustomerId.toString(),
        acquirerId = "GPS",
        code = "an acquirer customer id",
        createDate = datetime,
        updateDate = datetime
    )

val aCustomerResponse =
    CustomerResponse(
        id = aCustomerId,
        legalType = LEGAL_ENTITY,
        country = Country.ARG,
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
        status = Status.ACTIVE,
        deleteDate = null,
        createDate = datetime,
        updateDate = datetime
    )

fun anUpdatedCustomerResponse(now: OffsetDateTime) =
    CustomerResponse(
        id = aCustomerId,
        legalType = LEGAL_ENTITY,
        country = Country.ARG,
        businessName = "another business name",
        fantasyName = "another fantasy name",
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
        status = Status.ACTIVE,
        deleteDate = null,
        createDate = datetime,
        updateDate = now
    )

fun anAcquirerCustomerRequest() =
    AcquirerCustomerRequest(
        acquirerId = "GPS",
        code = "an acquirer customer id"
    )

val aBusinessNameUpdateRequest =
    UpdateRequest(
        businessName = "another business name",
        fantasyName = "another fantasy name"
    )

val anUpdateRequest =
    UpdateRequest(
        country = Country.ARG,
        legalType = LEGAL_ENTITY,
        businessName = "another business name",
        fantasyName = "another fantasy name",
        tax = UpdateRequest.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = UpdateRequest.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = UpdateRequest.Representative(
            representativeId = UpdateRequest.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = UpdateRequest.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = UpdateRequest.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = UpdateRequest.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        )
    )

val anUpdatedCustomer = aCustomerCreated.copy(updateDate = OffsetDateTime.now())

val anotherUpdatedCustomer =
    Customer(
        id = aCustomerId,
        country = Country.ARG,
        legalType = LEGAL_ENTITY,
        businessName = "another business name",
        fantasyName = "another fantasy name",
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
        status = Status . ACTIVE,
        deleteDate = null,
        createDate = datetime,
        updateDate = datetime
    )

fun aCustomerRequest() =
    CustomerRequest(
        country = Country.ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = CustomerRequest.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = CustomerRequest.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = CustomerRequest.Representative(
            representativeId = CustomerRequest.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = CustomerRequest.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = CustomerRequest.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = CustomerRequest.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        )
    )

val aPreCustomer =
    PreCustomer(
        country = Country.ARG,
        legalType = LEGAL_ENTITY,
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = PreCustomer.Tax("a type", "a tax id"),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = PreCustomer.Address("a state", "a city", "a zip", "a street", "a number", "", ""),
        representative = PreCustomer.Representative(
            representativeId = PreCustomer.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = PreCustomer.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = PreCustomer.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = PreCustomer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        )
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiErrorResponse.ApiError(
                resource = "a resource",
                message = "a message",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )
