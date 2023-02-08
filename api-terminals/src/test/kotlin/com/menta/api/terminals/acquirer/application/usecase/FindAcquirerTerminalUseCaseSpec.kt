package com.menta.api.terminals.acquirer.application.usecase

import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.acquirer.anAcquirerId
import com.menta.api.terminals.acquirer.anAcquirerTerminal
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.acquirerTerminalNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class FindAcquirerTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerTerminalRepositoryOutPort>()
    val useCase = FindAcquirerTerminalUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find by acquirer terminal id") {

        scenario("terminal found") {

            every { repository.findBy(anAcquirerId, aTerminalId) } returns Optional.of(anAcquirerTerminal)

            useCase.execute(anAcquirerId, aTerminalId) shouldBeRight anAcquirerTerminal

            verify(exactly = 1) { repository.findBy(anAcquirerId, aTerminalId) }
        }

        scenario("terminal NOT found") {

            every { repository.findBy(anAcquirerId, aTerminalId) } returns Optional.empty()

            useCase.execute(anAcquirerId, aTerminalId) shouldBeLeft acquirerTerminalNotFound(anAcquirerId, aTerminalId)

            verify(exactly = 1) { repository.findBy(anAcquirerId, aTerminalId) }
        }
    }
})
