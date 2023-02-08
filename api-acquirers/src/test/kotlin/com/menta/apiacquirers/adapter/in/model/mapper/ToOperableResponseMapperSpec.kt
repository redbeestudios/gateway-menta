package com.menta.apiacquirers.adapter.`in`.model.mapper

import com.menta.apiacquirers.adapter.`in`.model.OperableResponse
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.domain.AcquirerCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOperableResponseMapperSpec : FeatureSpec({

    feature("mapFrom acquirer customer to operable response") {

        val mapper = ToOperableResponseMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val acquirer = AcquirerCustomer(
                customerId = customerId,
                acquirers = listOf(
                    AcquirerCustomer.Acquirer(
                        acquirerId = "GPS",
                        code = "a code"
                    )
                )
            )

            mapper.mapFrom(acquirer) shouldBe OperableResponse(
                customerId = customerId,
                acquirers = listOf(
                    OperableResponse.Acquirer(
                        acquirerId = "GPS",
                        code = "a code"
                    )
                )
            )
        }
    }
})
