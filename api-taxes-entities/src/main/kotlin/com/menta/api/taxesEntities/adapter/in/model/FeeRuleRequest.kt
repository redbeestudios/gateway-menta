package com.menta.api.taxesEntities.adapter.`in`.model

import com.menta.api.taxesEntities.domain.PaymentMethod
import java.math.BigDecimal

data class FeeRuleRequest(
    val paymentMethod: PaymentMethod,
    val term: Int,
    val installments: Int,
    val commission: BigDecimal,
    val mentaCommission: BigDecimal,
    val discount: BigDecimal
)
