package com.menta.apiacquirers.adapter.out.mapper

import com.menta.apiacquirers.adapter.out.model.AcquirerCustomerResponse
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.domain.AcquirerCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAcquirerCustomerMapperSpec : FeatureSpec({

    feature("mapFrom acquirer customer response to acquirer customer") {

        val mapper = ToAcquirerCustomerMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val response = AcquirerCustomerResponse(
                customerId = customerId,
                acquirerId = "GPS",
                code = "a code"
            )

            mapper.mapFrom(response) shouldBe AcquirerCustomer(
                customerId = customerId,
                acquirers = listOf(
                    AcquirerCustomer.Acquirer(
                        acquirerId = "GPS",
                        code = "a code"
                    )
                )
            )
        }
    }
})
