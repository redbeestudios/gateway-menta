package com.menta.api.customers.acquirer.adapter.`in`.model.mapper

import com.menta.api.customers.aCustomerId
import com.menta.api.customers.aPreAcquirerCustomer
import com.menta.api.customers.anAcquirerCustomerRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreAcquirerCustomerMapperSpec : FeatureSpec({

    val mapper = ToPreAcquirerCustomerMapper()

    feature("map AcquirerCustomerRequest to PreAcquirerCustomer") {

        scenario("successful map") {
            mapper.map(
                anAcquirerCustomerRequest(), aCustomerId
            ) shouldBe aPreAcquirerCustomer()
        }
    }
})
