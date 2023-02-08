package com.kiwi.api.reversal.hexagonal.application.usecase.entities

import com.kiwi.api.reversal.hexagonal.application.aReceivedTerminal
import com.kiwi.api.reversal.hexagonal.application.port.out.TerminalRepositoryPortOut
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindTerminalUseCaseSpec : FeatureSpec({

    feature("terminal search") {

        lateinit var terminalRepository: TerminalRepositoryPortOut

        lateinit var useCase: FindTerminalUseCase

        beforeEach {
            terminalRepository = mockk()

            useCase = FindTerminalUseCase(
                terminalRepository = terminalRepository
            )
        }

        scenario("successful terminal search") {
            val terminal = aReceivedTerminal()
            val terminalId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { terminalRepository.retrieve(terminalId) } returns terminal

            // expect that
            useCase.execute(terminalId) shouldBe terminal

            // dependencies called
            verify(exactly = 1) { terminalRepository.retrieve(terminalId) }
        }

        scenario("terminal not FOUND") {
            val terminalId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { terminalRepository.retrieve(terminalId) } throws InternalError("Terminal not found")

            // expect that
            shouldThrow<InternalError> { useCase.execute(terminalId) }

            // dependencies called
            verify(exactly = 1) { terminalRepository.retrieve(terminalId) }
        }
    }
})
