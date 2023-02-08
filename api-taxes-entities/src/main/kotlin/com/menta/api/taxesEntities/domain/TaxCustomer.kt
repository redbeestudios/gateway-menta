package com.menta.api.taxesEntities.domain

import nonapi.io.github.classgraph.json.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("customersTaxes")
data class TaxCustomer(
    @Id
    val id: UUID,
    val customerId: UUID,
    val country: Country,
    val taxCondition: FiscalCondition,
    val feeRules: List<FeeRule>?,
    val settlementCondition: SettlementCondition?,
    val merchantFeeRulesOptions: List<FeeRule>?
)
