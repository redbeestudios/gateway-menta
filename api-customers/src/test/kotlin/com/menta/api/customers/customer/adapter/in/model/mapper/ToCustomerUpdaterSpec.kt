package com.menta.api.customers.customer.adapter.`in`.model.mapper

import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.anUpdateRequest
import com.menta.api.customers.anotherUpdatedCustomer
import com.menta.api.customers.customer.adapter.`in`.model.UpdateRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToCustomerUpdaterSpec : FeatureSpec({

    val updater = ToCustomerUpdater()

    feature("applyChanges") {

        scenario("with a full replacement") {
            updater
                .applyChanges(aCustomerCreated, anUpdateRequest) shouldBe anotherUpdatedCustomer
        }

        scenario("with no replacements") {
            updater
                .applyChanges(aCustomerCreated, UpdateRequest()) shouldBe aCustomerCreated
        }
    }
})
