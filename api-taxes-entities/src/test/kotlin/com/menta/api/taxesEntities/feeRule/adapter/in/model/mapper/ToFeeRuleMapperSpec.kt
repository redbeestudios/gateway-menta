package com.menta.api.taxesEntities.feeRule.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aFeeRuleRequest
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToFeeRuleMapper
import com.menta.api.taxesEntities.domain.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ToFeeRuleMapperSpec : FeatureSpec({
    val provider: IdProvider = mockk()
    val mapper = ToFeeRuleMapper(provider)

    feature("map ToFeeRuleMapper to FeeRule") {

        val aFeeRuleRequest = aFeeRuleRequest()

        scenario("successful map") {
            every { provider.provide() } returns aTaxCustomer().feeRules?.get(0)?.id!!
            mapper.mapFrom(aFeeRuleRequest) shouldBe aTaxCustomer().feeRules!![0]
        }
    }
})
