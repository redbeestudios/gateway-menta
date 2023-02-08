package com.menta.api.taxesEntities.taxMerchant.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.aTaxMerchantResponse
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantResponseMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class ToTaxMerchantResponseMapperSpec : FeatureSpec({

    val mapper = ToTaxMerchantResponseMapper()

    feature("map tax merchant to response") {
        scenario("successful map") {
            mapper.mapFrom(
                aTaxMerchant().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))
            ) shouldBe aTaxMerchantResponse()
        }
    }
})
