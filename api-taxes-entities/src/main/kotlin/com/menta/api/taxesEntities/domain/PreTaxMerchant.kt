package com.menta.api.taxesEntities.domain

import java.util.UUID

data class PreTaxMerchant(
    val taxCondition: FiscalCondition,
    val settlementCondition: SettlementCondition?,
    val customerId: UUID,
    val country: Country,
    val feeRules: List<FeeRule>?,
)
