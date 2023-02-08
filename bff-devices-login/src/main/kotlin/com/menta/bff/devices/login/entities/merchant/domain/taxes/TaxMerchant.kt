package com.menta.bff.devices.login.entities.merchant.domain.taxes

import com.menta.bff.devices.login.shared.domain.Country
import java.math.BigDecimal
import java.util.UUID

data class TaxMerchant(
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val country: Country,
    val taxCondition: FiscalCondition,
    val feeRules: List<FeeRule>?
) {
    data class FeeRule(
        val paymentMethod: PaymentMethod,
        val term: Int,
        val installments: Int,
        val commission: BigDecimal,
        val mentaCommission: BigDecimal,
        val discount: BigDecimal
    )
}
