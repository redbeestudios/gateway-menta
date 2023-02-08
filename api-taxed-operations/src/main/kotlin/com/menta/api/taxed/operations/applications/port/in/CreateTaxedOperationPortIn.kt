package com.menta.api.taxed.operations.applications.port.`in`

import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation

interface CreateTaxedOperationPortIn {

    fun execute(paymentTaxedOperation: PaymentTaxedOperation): TaxedOperation
}
