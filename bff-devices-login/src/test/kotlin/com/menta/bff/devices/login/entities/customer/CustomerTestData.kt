package com.menta.bff.devices.login.entities.customer

import com.menta.bff.devices.login.entities.customer.domain.Customer
import java.time.OffsetDateTime
import java.util.UUID

val customerId = UUID.fromString("4037a1d0-df4c-4ea6-844e-b37e22733cd1")
fun aCustomer() =
    Customer(
        id = customerId,
        country = "ARG",
        legalType = "LEGAL_ENTITY",
        businessName = "a business name",
        fantasyName = "a fantasy name",
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
        address = Customer.Address(
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
        tax = Customer.Tax("id", "a type"),
        settlementCondition = Customer.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "ACTIVE",
        deleteDate = null
    )
