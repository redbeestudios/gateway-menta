package com.menta.api.terminals.applications.usecase

import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class FindTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<TerminalRepositoryOutPort>()
    val useCase = FindTerminalUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find by terminal id") {

        scenario("terminal found") {

            every { repository.findBy(aTerminalId) } returns Optional.of(aTerminal)

            useCase.execute(aTerminalId) shouldBeRight aTerminal

            verify(exactly = 1) { repository.findBy(aTerminalId) }
        }

        scenario("terminal NOT found") {

            every { repository.findBy(aTerminalId) } returns Optional.empty()

            useCase.execute(aTerminalId) shouldBeLeft terminalNotFound(aTerminalId)

            verify(exactly = 1) { repository.findBy(aTerminalId) }
        }
    }

    feature("find by univocity") {

        scenario("terminal found") {
            val terminal = aTerminal

            every { repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) } returns Optional.of(terminal)

            useCase.findByUnivocity(terminal.serialCode, terminal.tradeMark, terminal.model) shouldBe Optional.of(terminal)

            verify(exactly = 1) { repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) }
        }

        scenario("terminal NOT found") {
            val terminal = aTerminal

            every { repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) } returns Optional.empty()

            useCase.findByUnivocity(terminal.serialCode, terminal.tradeMark, terminal.model) shouldBe Optional.empty()

            verify(exactly = 1) { repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) }
        }
    }
})
