package com.menta.api.taxed.operations.domain

import java.math.BigDecimal
import java.util.UUID

data class TaxRule(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val percentage: BigDecimal = BigDecimal.ZERO,
    val active: Boolean = true,
    val fiscalCondition: FiscalCondition,
    val country: Country,
    val paymentMethod: PaymentMethod,
    val taxType: TaxType,
    val taxFree: Boolean
)
