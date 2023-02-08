package com.menta.api.customers.customer.domain.resolver

import com.menta.api.customers.aCustomerCreated
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldNotBe

class CustomerDeletionResolverSpec : FeatureSpec({

    val resolver = CustomerDeletionResolver()

    feature("resolveDeletion") {

        scenario("with customer") {
            resolver.resolveDeletion(
                aCustomerCreated
            ).deleteDate shouldNotBe null
        }
    }
})
