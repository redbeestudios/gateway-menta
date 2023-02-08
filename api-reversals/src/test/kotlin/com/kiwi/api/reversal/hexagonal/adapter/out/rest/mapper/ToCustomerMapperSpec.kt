package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.application.aCustomer
import com.kiwi.api.reversal.hexagonal.application.aCustomerResponse
import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
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
                Customer(
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
