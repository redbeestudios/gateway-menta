package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerRequest
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.domain.provider.IdProvider
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTaxCustomerMapper(
    private val idProvider: IdProvider
) {

    fun mapFrom(customerRequest: TaxCustomerRequest): TaxCustomer =
        with(customerRequest) {
            TaxCustomer(
                id = idProvider.provide(),
                customerId = customerId,
                country = country,
                taxCondition = taxCondition,
                feeRules = feeRules,
                settlementCondition = settlementCondition,
                merchantFeeRulesOptions = merchantFeeRulesOptions
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
