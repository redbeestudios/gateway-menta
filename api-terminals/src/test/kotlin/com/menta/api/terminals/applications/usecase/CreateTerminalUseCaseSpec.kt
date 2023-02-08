package com.menta.api.terminals.applications.usecase

import com.menta.api.terminals.aPreTerminal
import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.provider.IdProvider
import com.menta.api.terminals.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class CreateTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalRepositoryOutPort>()
    val mockIdProvider = mockk<IdProvider>()
    val useCase = CreateTerminalUseCase(repository, mockIdProvider)

    beforeEach { clearAllMocks() }

    feature("create terminal") {
        scenario("with terminal valid") {
            val preTerminal = aPreTerminal()
            val terminal = aTerminal

            every { mockIdProvider.provide() } returns aTerminalId
            every { repository.create(terminal) } returns terminal

            useCase.execute(preTerminal, Optional.empty()) shouldBeRight terminal

            verify(exactly = 1) { repository.create(terminal) }
            verify(exactly = 1) { mockIdProvider.provide() }
        }

        scenario("with terminal existing") {
            val preTerminal = aPreTerminal()
            val terminal = aTerminal

            every { mockIdProvider.provide() } returns aTerminalId
            every { repository.create(terminal) } returns terminal

            useCase.execute(preTerminal, Optional.of(terminal)) shouldBeLeft ApplicationError.terminalExists()

            verify(exactly = 0) { repository.create(any()) }
            verify(exactly = 0) { mockIdProvider.provide() }
        }
    }
})
