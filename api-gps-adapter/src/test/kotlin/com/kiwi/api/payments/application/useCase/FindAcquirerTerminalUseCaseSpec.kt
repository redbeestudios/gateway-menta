package com.kiwi.api.payments.application.useCase

import com.kiwi.api.payments.application.anAcquirerTerminal
import com.kiwi.api.payments.application.port.out.AcquirerTerminalRepositoryOutPort
import com.kiwi.api.payments.application.usecase.FindAcquirerTerminalUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindAcquirerTerminalUseCaseSpec : FeatureSpec({

    feature("Find Acquirer Terminal") {

        lateinit var acquirerRepository: AcquirerTerminalRepositoryOutPort

        lateinit var useCase: FindAcquirerTerminalUseCase

        beforeEach {
            acquirerRepository = mockk()

            useCase = FindAcquirerTerminalUseCase(
                repository = acquirerRepository
            )
        }

        scenario("Successful find") {
            val response = anAcquirerTerminal()
            // given mocked dependencies
            every { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns response

            // expect that
            useCase.execute(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) shouldBe response

            // dependencies called
            verify(exactly = 1) { acquirerRepository.findBy(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) }
        }
    }
})
