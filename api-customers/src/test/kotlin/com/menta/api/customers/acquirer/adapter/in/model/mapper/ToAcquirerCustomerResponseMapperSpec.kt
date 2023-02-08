package com.menta.api.customers.acquirer.adapter.`in`.model.mapper

import com.menta.api.customers.aCustomerId
import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerResponse
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.OffsetDateTime
import java.util.UUID

class ToAcquirerCustomerResponseMapperSpec : FeatureSpec({

    val mapper = ToAcquirerCustomerResponseMapper()

    feature("map acquirer customer to response") {

        scenario("successful map") {
            mapper.mapFrom(
                AcquirerCustomer(
                    id = aCustomerId,
                    customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
                    acquirerId = "GPS",
                    code = "a code",
                    createDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
                    updateDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00")
                )
            ) shouldBe AcquirerCustomerResponse(
                customerId = "0f14d0ab-9605-4a62-a9e4-5ed26688389b",
                acquirerId = "GPS",
                code = "a code",
                createDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
                updateDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00")
            )
        }
    }
})
