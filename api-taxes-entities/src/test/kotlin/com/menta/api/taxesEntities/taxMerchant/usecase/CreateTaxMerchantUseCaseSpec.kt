package com.menta.api.taxesEntities.taxMerchant.usecase

import arrow.core.right
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.CreateMerchantUseCase
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateTaxMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<TaxMerchantRepositoryOutPort>()
    val useCase = CreateMerchantUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("create tax merchant") {
        scenario("with tax merchant valid") {
            val taxMerchant = aTaxMerchant().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))

            every { repository.create(taxMerchant) } returns taxMerchant.right()

            useCase.execute(taxMerchant) shouldBeRight taxMerchant

            verify(exactly = 1) { repository.create(taxMerchant) }
        }
    }
})
