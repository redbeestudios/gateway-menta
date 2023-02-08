package com.menta.apiacquirers.adapter.out.mapper

import com.menta.apiacquirers.adapter.out.model.CustomerResponse
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.domain.Customer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import java.time.OffsetDateTime

class ToCustomerMapperSpec : FeatureSpec({

    feature("mapFrom customer response to customer") {

        val mapper = ToCustomerMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val response = CustomerResponse(
                id = customerId,
                country = "ARG",
                legalType = "LEGAL_ENTITY",
                businessName = "a business name",
                fantasyName = "a fantasy name",
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
                address = CustomerResponse.Address(
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
                tax = CustomerResponse.Tax("id", "a type"),
                settlementCondition = CustomerResponse.SettlementCondition(
                    transactionFee = "transactionFee",
                    settlement = "settlement",
                    cbuOrCvu = "cbuOrCvu"
                ),
                status = "ACTIVE",
                deleteDate = null
            )

            mapper.mapFrom(response) shouldBe Customer(
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
        }
    }
})
