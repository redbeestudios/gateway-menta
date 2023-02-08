package com.menta.api.taxesEntities.domain

import nonapi.io.github.classgraph.json.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("merchantsTaxes")
data class TaxMerchant(
    @Id
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val country: Country,
    val taxCondition: FiscalCondition,
    val feeRules: List<FeeRule>?,
    val settlementCondition: SettlementCondition?
)
