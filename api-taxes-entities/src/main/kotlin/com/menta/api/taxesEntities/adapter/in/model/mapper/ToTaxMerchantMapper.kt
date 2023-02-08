package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.provider.MerchantFeeRulesOptionProvider
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.domain.provider.IdProvider
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTaxMerchantMapper(
    private val idProvider: IdProvider,
    private val merchantFeeRulesOptionProvider: MerchantFeeRulesOptionProvider
) {

    fun mapFrom(merchantRequest: TaxMerchantRequest): TaxMerchant =
        with(merchantRequest) {
            TaxMerchant(
                id = idProvider.provide(),
                merchantId = merchantId,
                customerId = customerId,
                country = country,
                taxCondition = taxCondition,
                feeRules = merchantFeeRulesOptionProvider.provide(customerId, feeRules),
                settlementCondition = settlementCondition
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
