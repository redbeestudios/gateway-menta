package com.menta.api.taxesEntities.adapter.`in`.model

import com.menta.api.taxesEntities.domain.Country
import com.menta.api.taxesEntities.domain.FiscalCondition
import com.menta.api.taxesEntities.domain.SettlementCondition
import java.util.UUID

data class TaxMerchantRequest(
    val merchantId: UUID,
    val customerId: UUID,
    val country: Country,
    val taxCondition: FiscalCondition,
    val feeRules: List<UUID>?,
    val settlementCondition: SettlementCondition?
)
