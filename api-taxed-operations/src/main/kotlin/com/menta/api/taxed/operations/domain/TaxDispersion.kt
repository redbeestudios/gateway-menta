package com.menta.api.taxed.operations.domain

import java.math.BigDecimal
import java.time.OffsetDateTime

data class TaxDispersion(
    val taxedAmount: BigDecimal,
    val iva: BigDecimal,
    val ganancias: BigDecimal,
    val feeRule: FeeRule,
    val grossCommission: BigDecimal,
    val grossCommissionWithTax: BigDecimal,
    val partialGrossAmount: BigDecimal,
    val ivaCommission: BigDecimal,
    val nextPaymentDate: OffsetDateTime
)
