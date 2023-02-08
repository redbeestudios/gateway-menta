package com.menta.api.merchants.merchant.adapter.`in`.model.hateoas

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.adapter.`in`.model.MerchantResponse
import com.menta.api.merchants.adapter.`in`.model.hateos.MerchantModelAssembler
import com.menta.api.merchants.datetime
import com.menta.api.merchants.domain.Country
import com.menta.api.merchants.domain.LegalType
import com.menta.api.merchants.domain.provider.AppUrlProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.hateoas.Link
import java.time.OffsetDateTime
import java.util.UUID

class MerchantModelAssemblerSpec : FeatureSpec({

    feature("map acquirer merchant to response") {

        val appUrlProvider = mockk<AppUrlProvider>()
        val assembler = MerchantModelAssembler(appUrlProvider)

        scenario("successful map") {
            every { appUrlProvider.provide() } returns "localhost:8080/merchants"

            assembler.toModel(aMerchant()) shouldBe MerchantResponse(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
                customerId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
                country = Country.ARG,
                legalType = LegalType.LEGAL_ENTITY,
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
            ).add(Link.of("localhost:8080/merchants/0f14d0ab-9605-4a62-a9e4-5ed26688389b").withSelfRel())
        }
    }
})
