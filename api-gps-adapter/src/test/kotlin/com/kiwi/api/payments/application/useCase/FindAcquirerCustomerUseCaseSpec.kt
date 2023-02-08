package com.kiwi.api.payments.application.useCase

import com.kiwi.api.payments.application.anAcquirerCustomer
import com.kiwi.api.payments.application.port.out.AcquirerCustomerRepositoryOutPort
import com.kiwi.api.payments.application.usecase.FindAcquirerCustomerUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindAcquirerCustomerUseCaseSpec : FeatureSpec({

    feature("Find Acquirer Customer") {

        lateinit var acquirerRepository: AcquirerCustomerRepositoryOutPort

        lateinit var useCase: FindAcquirerCustomerUseCase

        beforeEach {
            acquirerRepository = mockk()

            useCase = FindAcquirerCustomerUseCase(
                repository = acquirerRepository
            )
        }

        scenario("Successful find") {
            // given mocked dependencies
            every { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns anAcquirerCustomer()

            // expect that
            useCase.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) shouldBe anAcquirerCustomer()

            // dependencies called
            verify(exactly = 1) { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) }
        }
    }
})
