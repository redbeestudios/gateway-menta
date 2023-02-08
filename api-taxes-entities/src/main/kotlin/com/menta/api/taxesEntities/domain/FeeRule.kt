package com.menta.api.taxesEntities.domain

import java.math.BigDecimal
import java.util.UUID

data class FeeRule(
    val id: UUID,
    val paymentMethod: PaymentMethod,
    val term: Int,
    val installments: Int,
    val commission: BigDecimal,
    val mentaCommission: BigDecimal,
    val discount: BigDecimal
)
