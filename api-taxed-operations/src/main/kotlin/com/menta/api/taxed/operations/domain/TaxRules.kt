package com.menta.api.taxed.operations.domain

data class TaxRules(
    val merchantTaxRules: List<TaxRule>?,
    val customerTaxRules: List<TaxRule>?
)
