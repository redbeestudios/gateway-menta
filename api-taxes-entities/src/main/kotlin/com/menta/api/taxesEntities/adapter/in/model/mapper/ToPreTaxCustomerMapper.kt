package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxCustomerRequest
import com.menta.api.taxesEntities.domain.PreTaxCustomer
import org.springframework.stereotype.Component

@Component
class ToPreTaxCustomerMapper {

    fun map(preTaxCustomerRequest: PreTaxCustomerRequest) =
        with(preTaxCustomerRequest) {
            PreTaxCustomer(
                country = country,
                feeRules = feeRules,
                settlementCondition = settlementCondition,
                taxCondition = taxCondition
            )
        }
}
