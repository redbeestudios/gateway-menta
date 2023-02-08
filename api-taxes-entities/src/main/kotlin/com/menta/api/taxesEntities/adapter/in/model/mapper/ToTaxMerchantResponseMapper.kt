package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantResponse
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTaxMerchantResponseMapper {

    fun mapFrom(taxMerchant: TaxMerchant) =
        with(taxMerchant) {
            TaxMerchantResponse(
                id = id,
                merchantId = merchantId,
                customerId = customerId,
                country = country,
                taxCondition = taxCondition,
                feeRules = feeRules,
                settlementCondition = settlementCondition
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
