package com.menta.api.taxesEntities.adapter.`in`.model

import com.menta.api.taxesEntities.domain.Country
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.domain.FiscalCondition
import com.menta.api.taxesEntities.domain.SettlementCondition

data class PreTaxCustomerRequest(
    val country: Country,
    val feeRules: List<FeeRule>?,
    val settlementCondition: SettlementCondition?,
    val taxCondition: FiscalCondition
)
