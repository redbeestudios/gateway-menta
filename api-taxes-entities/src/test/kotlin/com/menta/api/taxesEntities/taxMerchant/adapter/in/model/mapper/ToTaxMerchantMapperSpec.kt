package com.menta.api.taxesEntities.taxMerchant.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.aTaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantMapper
import com.menta.api.taxesEntities.adapter.`in`.model.provider.MerchantFeeRulesOptionProvider
import com.menta.api.taxesEntities.domain.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class ToTaxMerchantMapperSpec : FeatureSpec({
    val idProvider = mockk<IdProvider>()
    val merchantFeeRulesOptionProvider: MerchantFeeRulesOptionProvider = mockk()
    val mapper = ToTaxMerchantMapper(idProvider, merchantFeeRulesOptionProvider)

    feature("map TaxMerchantRequest to TaxMerchant") {

        scenario("successful map") {
            val merchantRequest = aTaxMerchantRequest()
            every { idProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            every { merchantFeeRulesOptionProvider.provide(merchantRequest.customerId, merchantRequest.feeRules) } returns aTaxCustomer().feeRules!!

            mapper.mapFrom(merchantRequest) shouldBe aTaxMerchant()
        }
    }
})
