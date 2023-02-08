package com.menta.api.taxed.operations.domain

import java.util.UUID

data class PaymentTaxedOperation(
    val paymentId: UUID,
    val terminalId: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val grossAmount: String,
    val currency: String,
    val installments: String,
    val merchantDispersion: TaxDispersion,
    val customerDispersion: TaxDispersion,
    val taxes: TaxRules
)
