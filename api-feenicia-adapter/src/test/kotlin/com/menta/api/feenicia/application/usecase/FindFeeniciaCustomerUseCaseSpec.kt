package com.menta.api.feenicia.application.usecase

import com.menta.api.feenicia.application.aFeeniciaMerchant
import com.menta.api.feenicia.application.port.out.FeeniciaMerchantClientRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindFeeniciaMerchantUseCaseSpec : FeatureSpec({

    feature("Find Feenicia Merchant") {

        lateinit var feeniciaMerchantClientRepository: FeeniciaMerchantClientRepository

        lateinit var useCase: FindFeeniciaMerchantUseCase

        beforeEach {
            feeniciaMerchantClientRepository = mockk()

            useCase = FindFeeniciaMerchantUseCase(
                repository = feeniciaMerchantClientRepository
            )
        }

        scenario("Successful find") {
            // given mocked dependencies
            every { feeniciaMerchantClientRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns aFeeniciaMerchant()

            // expect that
            useCase.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) shouldBe aFeeniciaMerchant()

            // dependencies called
            verify(exactly = 1) { feeniciaMerchantClientRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) }
        }
    }
})
