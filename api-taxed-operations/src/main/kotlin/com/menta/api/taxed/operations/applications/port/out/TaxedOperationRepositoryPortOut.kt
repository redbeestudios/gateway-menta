package com.menta.api.taxed.operations.applications.port.out

import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation

interface TaxedOperationRepositoryPortOut {

    fun create(paymentTaxedOperation: PaymentTaxedOperation): TaxedOperation
}
