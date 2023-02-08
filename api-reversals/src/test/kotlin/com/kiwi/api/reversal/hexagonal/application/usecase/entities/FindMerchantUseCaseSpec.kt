package com.kiwi.api.reversal.hexagonal.application.usecase.entities

import com.kiwi.api.reversal.hexagonal.application.aMerchant
import com.kiwi.api.reversal.hexagonal.application.port.out.MerchantRepositoryPortOut
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindMerchantUseCaseSpec : FeatureSpec({

    feature("merchant search") {

        lateinit var merchantRepository: MerchantRepositoryPortOut

        lateinit var useCase: FindMerchantUseCase

        beforeEach {
            merchantRepository = mockk()

            useCase = FindMerchantUseCase(
                merchantRepository = merchantRepository
            )
        }

        scenario("successful merchant search") {
            val merchant = aMerchant()
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { merchantRepository.retrieve(merchantId) } returns merchant

            // expect that
            useCase.execute(merchantId) shouldBe merchant

            // dependencies called
            verify(exactly = 1) { merchantRepository.retrieve(merchantId) }
        }

        scenario("merchant not FOUND") {
            val merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { merchantRepository.retrieve(merchantId) } throws InternalError("Merchant not found")

            // expect that
            shouldThrow<InternalError> { useCase.execute(merchantId) }

            // dependencies called
            verify(exactly = 1) { merchantRepository.retrieve(merchantId) }
        }
    }
})
