package com.menta.api.taxed.operations.adapter.out.db

import com.menta.api.taxed.operations.adapter.out.db.mapper.ToTaxedOperationMapper
import com.menta.api.taxed.operations.adapter.out.db.resolver.CreateDatetimeResolver
import com.menta.api.taxed.operations.application.aPaymentTaxedOperation
import com.menta.api.taxed.operations.application.aTaxedOperation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TaxedOperationRepositorySpec : FeatureSpec({
    val dbRepository: TaxedOperationDbRepository = mockk()
    val repository = TaxedOperationRepository(
        repository =
        dbRepository,
        toTaxedOperationMapper = ToTaxedOperationMapper(
            createDatetimeResolver = CreateDatetimeResolver()
        )
    )

    beforeEach { clearAllMocks() }

    feature("create taxed operation") {
        scenario("taxed operation created") {
            val paymentTaxedOperation = aPaymentTaxedOperation()
            val response = aTaxedOperation()

            every { dbRepository.save(response) } returns response

            repository.create(paymentTaxedOperation) shouldBe response

            verify(exactly = 1) { dbRepository.save(response) }
        }
    }
})
