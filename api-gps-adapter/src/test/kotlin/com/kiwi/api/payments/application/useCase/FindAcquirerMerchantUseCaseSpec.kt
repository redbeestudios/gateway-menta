package com.kiwi.api.payments.application.useCase

import com.kiwi.api.payments.application.anAcquirerMerchant
import com.kiwi.api.payments.application.port.out.AcquirerMerchantRepositoryOutPort
import com.kiwi.api.payments.application.usecase.FindAcquirerMerchantUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindAcquirerMerchantUseCaseSpec : FeatureSpec({

    feature("Find Acquirer Merchant") {

        lateinit var acquirerRepository: AcquirerMerchantRepositoryOutPort

        lateinit var useCase: FindAcquirerMerchantUseCase

        beforeEach {
            acquirerRepository = mockk()

            useCase = FindAcquirerMerchantUseCase(
                repository = acquirerRepository
            )
        }

        scenario("Successful find") {
            // given mocked dependencies
            every { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns anAcquirerMerchant()

            // expect that
            useCase.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) shouldBe anAcquirerMerchant()

            // dependencies called
            verify(exactly = 1) { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) }
        }
    }
})
