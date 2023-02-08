package com.menta.api.merchants.merchant.application.usecase

import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.application.usecase.FindMerchantUseCase
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.merchantNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class FindMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<MerchantRepositoryOutPort>()
    val useCase = FindMerchantUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find merchant") {

        scenario("merchant found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val merchant = aMerchant()
            every { repository.findBy(merchantId) } returns Optional.of(merchant)

            useCase.execute(merchantId) shouldBeRight merchant

            verify(exactly = 1) { repository.findBy(merchantId) }
        }

        scenario("merchant NOT found") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val error = merchantNotFound(merchantId)
            every { repository.findBy(merchantId) } returns Optional.empty()

            useCase.execute(merchantId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(merchantId) }
        }
    }
})
