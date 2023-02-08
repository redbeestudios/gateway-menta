package com.menta.api.taxesEntities.adapter.`in`.model.mapper

import com.menta.api.taxesEntities.adapter.`in`.model.FeeRuleRequest
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.domain.provider.IdProvider
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToFeeRuleMapper(
    private val idProvider: IdProvider
) {

    fun mapFrom(feeRuleRequest: FeeRuleRequest): FeeRule =
        with(feeRuleRequest) {
            FeeRule(
                idProvider.provide(),
                paymentMethod,
                term,
                installments,
                commission,
                mentaCommission,
                discount
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
