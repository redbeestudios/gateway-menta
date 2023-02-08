package com.kiwi.api.payments.hexagonal.adapter.port.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToCustomerMapper
import com.kiwi.api.payments.hexagonal.application.aCustomer
import com.kiwi.api.payments.hexagonal.application.aCustomerResponse
import com.kiwi.api.payments.hexagonal.domain.Payment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToCustomerMapperSpec : FeatureSpec({

    feature("map customer") {

        val mapper = ToCustomerMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val customerResponse = aCustomerResponse()
            val customer = aCustomer()

            mapper.map(customerResponse) shouldBe with(customer) {
                Payment.Customer(
                    id = id,
                    country = country,
                    legalType = legalType,
                    businessName = businessName,
                    fantasyName = fantasyName,
                    tax = tax,
                    activity = activity,
                    email = email,
                    phone = phone,
                    address = address,
                    businessOwner = businessOwner,
                    representative = representative,
                    settlementCondition = settlementCondition,
                    status = status
                )
            }
        }
    }
})
