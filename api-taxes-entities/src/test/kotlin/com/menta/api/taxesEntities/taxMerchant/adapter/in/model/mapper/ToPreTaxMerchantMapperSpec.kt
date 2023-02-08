package com.menta.api.taxesEntities.taxMerchant.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aPreTaxMerchant
import com.menta.api.taxesEntities.aPreTaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreMerchantMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreTaxMerchantMapperSpec : FeatureSpec({

    val mapper = ToPreMerchantMapper()

    feature("map ToPreTaxCustomerMapper to PreTaxCustomer") {

        val aPreTaxMerchantRequest = aPreTaxMerchantRequest()

        scenario("successful map") {

            mapper.map(aPreTaxMerchantRequest) shouldBe aPreTaxMerchant()
        }
    }
})
