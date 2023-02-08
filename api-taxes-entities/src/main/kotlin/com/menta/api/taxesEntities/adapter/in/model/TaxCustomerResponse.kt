package com.menta.api.taxesEntities.adapter.`in`.model

import com.menta.api.taxesEntities.domain.Country
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.domain.FiscalCondition
import com.menta.api.taxesEntities.domain.SettlementCondition
import java.util.UUID

data class TaxCustomerResponse(
    val id: UUID,
    val customerId: UUID,
    val country: Country,
    val taxCondition: FiscalCondition,
    val feeRules: List<FeeRule>?,
    val settlementCondition: SettlementCondition?
)
