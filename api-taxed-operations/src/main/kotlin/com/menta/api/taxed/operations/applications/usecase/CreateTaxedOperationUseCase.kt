package com.menta.api.taxed.operations.applications.usecase

import com.menta.api.taxed.operations.applications.port.`in`.CreateTaxedOperationPortIn
import com.menta.api.taxed.operations.applications.port.out.TaxedOperationRepositoryPortOut
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import org.springframework.stereotype.Component

@Component
class CreateTaxedOperationUseCase(
    private val taxedOperationRepository: TaxedOperationRepositoryPortOut
) : CreateTaxedOperationPortIn {

    override fun execute(paymentTaxedOperation: PaymentTaxedOperation) =
        taxedOperationRepository.create(paymentTaxedOperation)
}
