package com.menta.api.taxesEntities.taxCustomer.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aPreTaxCustomer
import com.menta.api.taxesEntities.aPreTaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreTaxCustomerMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreTaxCustomerMapperSpec : FeatureSpec({

    val mapper = ToPreTaxCustomerMapper()

    feature("map ToPreTaxCustomerMapper to PreTaxCustomer") {

        val aPreTaxCustomerRequest = aPreTaxCustomerRequest()

        scenario("successful map") {

            mapper.map(aPreTaxCustomerRequest) shouldBe aPreTaxCustomer()
        }
    }
})
