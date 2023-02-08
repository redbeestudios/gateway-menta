package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.aCustomerRequest
import com.menta.api.customers.aPreCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreCustomerMapperSpec : FeatureSpec({

    val mapper = ToPreCustomerMapper()

    feature("map CustomerRequest to PreCustomer") {

        scenario("successful map") {
            mapper.map(
                aCustomerRequest()
            ) shouldBe aPreCustomer
        }
    }
})
