package com.menta.api.taxed.operations.application.usecase

import com.menta.api.taxed.operations.application.aPaymentTaxedOperation
import com.menta.api.taxed.operations.application.aTaxedOperation
import com.menta.api.taxed.operations.applications.port.out.TaxedOperationRepositoryPortOut
import com.menta.api.taxed.operations.applications.usecase.CreateTaxedOperationUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateTaxedOperationUseCaseSpec : FeatureSpec({
    val repository: TaxedOperationRepositoryPortOut = mockk()

    val useCase = CreateTaxedOperationUseCase(repository)

    feature("create operation") {
        scenario("create operation successfull") {
            val paymentTaxedOperation = aPaymentTaxedOperation()
            val response = aTaxedOperation()

            every { repository.create(paymentTaxedOperation) } returns response

            useCase.execute(paymentTaxedOperation) shouldBe response

            verify(exactly = 1) { repository.create(paymentTaxedOperation) }
        }
    }
})
