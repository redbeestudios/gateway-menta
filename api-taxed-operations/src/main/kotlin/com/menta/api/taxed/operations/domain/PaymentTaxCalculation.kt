package com.menta.api.taxed.operations.domain

data class PaymentTaxCalculation(
    val id: String,
    val createdPayment: CreatedPayment,
    val taxCalculation: TaxCalculation,
    val taxes: TaxRules,
)
