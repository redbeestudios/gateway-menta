package com.menta.api.taxed.operations.domain

import java.math.BigDecimal

data class TaxCalculation(
    val grossAmount: BigDecimal,
    val merchantTaxDispersion: TaxDispersion,
    val customerTaxDispersion: TaxDispersion
)
