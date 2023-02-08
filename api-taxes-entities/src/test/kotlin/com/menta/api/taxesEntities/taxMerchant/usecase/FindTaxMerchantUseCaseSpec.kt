package com.menta.api.taxesEntities.taxMerchant.usecase

import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.FindMerchantUseCase
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class FindTaxMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<TaxMerchantRepositoryOutPort>()
    val useCase = FindMerchantUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find merchant") {

        scenario("merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val taxMerchant = aTaxMerchant()
            every { repository.findBy(merchantId) } returns Optional.of(taxMerchant)

            useCase.execute(merchantId) shouldBeRight taxMerchant

            verify(exactly = 1) { repository.findBy(merchantId) }
        }

        scenario("merchant NOT found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val error = notFound(merchantId)
            every { repository.findBy(merchantId) } returns Optional.empty()

            useCase.execute(merchantId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(merchantId) }
        }
    }
})
