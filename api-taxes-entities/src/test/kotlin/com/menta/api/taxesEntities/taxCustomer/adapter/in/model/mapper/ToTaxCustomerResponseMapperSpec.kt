package com.menta.api.taxesEntities.taxCustomer.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxCustomerResponse
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerResponseMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class ToTaxCustomerResponseMapperSpec : FeatureSpec({

    val mapper = ToTaxCustomerResponseMapper()

    feature("map tax customer to response") {
        scenario("successful map") {
            mapper.mapFrom(
                aTaxCustomer().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))
            ) shouldBe aTaxCustomerResponse()
        }
    }
})
