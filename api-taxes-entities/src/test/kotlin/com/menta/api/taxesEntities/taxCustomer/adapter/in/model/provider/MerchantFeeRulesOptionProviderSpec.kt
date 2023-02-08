package com.menta.api.taxesEntities.taxCustomer.adapter.`in`.model.provider

import arrow.core.right
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.adapter.`in`.model.provider.MerchantFeeRulesOptionProvider
import com.menta.api.taxesEntities.application.port.`in`.FindTaxCustomerPortIn
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class MerchantFeeRulesOptionProviderSpec : FeatureSpec({
    val taxCustomerPortIn: FindTaxCustomerPortIn = mockk()
    val provider = MerchantFeeRulesOptionProvider(taxCustomerPortIn)

    feature("map ToPreTaxCustomerMapper to PreTaxCustomer") {
        val taxCustomer = aTaxCustomer()
        every { taxCustomerPortIn.execute(taxCustomer.customerId) } returns taxCustomer.right()

        scenario("successful provider when is not null") {
            provider.provide(
                taxCustomer.customerId,
                listOf(UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"))
            ) shouldBe taxCustomer.merchantFeeRulesOptions
        }

        scenario("successful provider when is null") {
            provider.provide(taxCustomer.customerId, null) shouldBe null
        }
    }
})
