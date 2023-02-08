package com.menta.api.merchants.adapter.`in`.model.mapper

import com.menta.api.merchants.adapter.`in`.model.hateos.MerchantModelAssembler
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.provider.AppUrlProvider
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.stereotype.Component

@Component
class ToMerchantResponseMapper(
    private val pagedResourcesAssembler: PagedResourcesAssembler<Merchant>,
    private val merchantModelAssembler: MerchantModelAssembler,
    private val appUrlProvider: AppUrlProvider
) {

    fun map(merchants: Page<Merchant>) =
        pagedResourcesAssembler.toModel(merchants, merchantModelAssembler,
            Link.of(appUrlProvider.provide()).withSelfRel())

    fun map(merchant: Merchant) =
        merchantModelAssembler.toModel(merchant)
}
