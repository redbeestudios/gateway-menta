package com.menta.api.taxesEntities.taxCustomer.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.aTaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerMapper
import com.menta.api.taxesEntities.domain.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class ToTaxCustomerMapperSpec : FeatureSpec({

    val idProvider = mockk<IdProvider>()
    val mapper = ToTaxCustomerMapper(idProvider)

    feature("map ToTaxCustomerMapper to TaxCustomer") {

        scenario("successful map") {
            val customerRequest = aTaxCustomerRequest()
            every { idProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            mapper.mapFrom(customerRequest) shouldBe aTaxCustomer()
        }
    }
})
