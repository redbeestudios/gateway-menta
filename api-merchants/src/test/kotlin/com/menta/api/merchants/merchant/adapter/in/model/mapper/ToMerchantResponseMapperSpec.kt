package com.menta.api.merchants.merchant.adapter.`in`.model.mapper

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.aMerchantResponse
import com.menta.api.merchants.adapter.`in`.model.MerchantResponse
import com.menta.api.merchants.adapter.`in`.model.hateos.MerchantModelAssembler
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantResponseMapper
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.provider.AppUrlProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel

class ToAcquirerMerchantResponseMapperSpec : FeatureSpec({

    val pagedResourcesAssembler: PagedResourcesAssembler<Merchant> = mockk()
    val merchantModelAssembler: MerchantModelAssembler = mockk()
    val appUrlProvider: AppUrlProvider = mockk()
    val mapper = ToMerchantResponseMapper(
        pagedResourcesAssembler = pagedResourcesAssembler,
        merchantModelAssembler = merchantModelAssembler,
        appUrlProvider = appUrlProvider
    )

    feature("map merchant to merchant response") {
        scenario("successful map") {

            val merchant = aMerchant()
            val response = aMerchantResponse()

            every { merchantModelAssembler.toModel(merchant) } returns response

            mapper.map(merchant) shouldBe response
        }
    }
    feature("map page merchant to  response") {
        scenario("successful map") {

            val merchant = PageImpl(
                listOf(aMerchant())
            )
            val response: PagedModel<MerchantResponse> = PagedModel.of(
                listOf(aMerchantResponse()),
                PagedModel.PageMetadata(10, 1, 1, 1)
            )

            val link = Link.of("localhost:8080/merchants").withSelfRel()

            every { pagedResourcesAssembler.toModel(merchant, merchantModelAssembler, link) } returns response
            every { appUrlProvider.provide() } returns "localhost:8080/merchants"

            mapper.map(merchant) shouldBe response
        }
    }
})
