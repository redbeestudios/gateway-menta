package com.menta.api.taxesEntities.domain

data class PreTaxCustomer(
    val country: Country,
    val feeRules: List<FeeRule>?,
    val settlementCondition: SettlementCondition?,
    val taxCondition: FiscalCondition
)
