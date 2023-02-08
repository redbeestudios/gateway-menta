package com.menta.api.taxed.operations.domain

import java.math.BigDecimal

data class FeeRule(
    val paymentMethod: PaymentMethod,
    val term: Int,
    val installments: Int,
    val commission: BigDecimal,
    val discount: BigDecimal
)
