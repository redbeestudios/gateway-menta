package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxMerchantRequest
import com.menta.api.taxesEntities.domain.PreTaxMerchant
import org.springframework.stereotype.Component

@Component
class ToPreMerchantMapper {

    fun map(preTaxMerchantRequest: PreTaxMerchantRequest) =
        with(preTaxMerchantRequest) {
            PreTaxMerchant(
                taxCondition = taxCondition,
                country = country,
                settlementCondition = settlementCondition,
                customerId = customerId,
                feeRules = feeRules
            )
        }
}
