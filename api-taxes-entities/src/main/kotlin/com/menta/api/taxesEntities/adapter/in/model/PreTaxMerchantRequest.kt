package com.menta.api.taxesEntities.adapter.`in`.model

import com.menta.api.taxesEntities.domain.Country
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.domain.FiscalCondition
import com.menta.api.taxesEntities.domain.SettlementCondition
import java.util.UUID

data class PreTaxMerchantRequest(
    val taxCondition: FiscalCondition,
    val country: Country,
    val settlementCondition: SettlementCondition?,
    val customerId: UUID,
    val feeRules: List<FeeRule>?
)
