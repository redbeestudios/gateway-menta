package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerResponse
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTaxCustomerResponseMapper {

    fun mapFrom(taxCustomer: TaxCustomer) =
        with(taxCustomer) {
            TaxCustomerResponse(
                id = id,
                customerId = customerId,
                country = country,
                taxCondition = taxCondition,
                feeRules = feeRules,
                settlementCondition = settlementCondition
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
