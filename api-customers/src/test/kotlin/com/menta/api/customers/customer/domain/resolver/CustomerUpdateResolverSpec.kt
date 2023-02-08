package com.menta.api.customers.customer.domain.resolver

import com.menta.api.customers.aCustomerCreated
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldNotBe

class CustomerUpdateResolverSpec : FeatureSpec({

    val resolver = CustomerUpdateResolver()

    feature("resolveUpdate") {

        scenario("with customer") {
            resolver.resolveUpdate(
                aCustomerCreated
            ).updateDate shouldNotBe aCustomerCreated.updateDate
        }
    }
})
