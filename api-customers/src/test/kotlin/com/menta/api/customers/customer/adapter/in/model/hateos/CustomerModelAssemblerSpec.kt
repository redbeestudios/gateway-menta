package com.menta.api.customers.customer.adapter.`in`.model.hateos

import com.menta.api.customers.aCustomerId
import com.menta.api.customers.customer.adapter.`in`.model.CustomerResponse
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.LegalType
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.customer.domain.provider.AppUrlProvider
import com.menta.api.customers.datetime
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.hateoas.Link
import java.time.OffsetDateTime

class CustomerModelAssemblerSpec : FeatureSpec({

    val appUrlProvider = mockk<AppUrlProvider>()
    val modelAssembler = CustomerModelAssembler(appUrlProvider)

    feature("map Customer to CustomerResponse") {
        scenario("successful map") {
            val customer = Customer(
                id = aCustomerId,
                country = Country.ARG,
                legalType = LegalType.LEGAL_ENTITY,
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

            every { appUrlProvider.provide() } returns "localhost:8080/customers"

            modelAssembler.toModel(customer) shouldBe CustomerResponse(
                id = aCustomerId,
                country = Country.ARG,
                legalType = LegalType.LEGAL_ENTITY,
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
            ).add(Link.of("localhost:8080/customers/$aCustomerId").withSelfRel())
        }
    }
})
