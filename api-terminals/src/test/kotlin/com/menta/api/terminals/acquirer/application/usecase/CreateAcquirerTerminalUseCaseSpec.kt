package com.menta.api.terminals.acquirer.application.usecase

import com.menta.api.terminals.aPreAcquirerTerminal
import com.menta.api.terminals.acquirer.anAcquirerTerminal
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.applications.mapper.ToAcquirerTerminalMapper
import com.menta.api.terminals.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class CreateAcquirerTerminalUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerTerminalRepositoryOutPort>()
    val toAcquirerTerminalMapper = mockk<ToAcquirerTerminalMapper>()
    val useCase = CreateAcquirerTerminalUseCase(repository, toAcquirerTerminalMapper)

    beforeEach { clearAllMocks() }

    feature("create acquirer terminal") {

        scenario("with pre acquirer terminal") {
            val preAcquirerTerminal = aPreAcquirerTerminal
            val acquirerTerminal = anAcquirerTerminal

            every { toAcquirerTerminalMapper.map(preAcquirerTerminal) } returns acquirerTerminal
            every { repository.create(acquirerTerminal) } returns acquirerTerminal

            useCase.execute(preAcquirerTerminal, Optional.empty()) shouldBeRight acquirerTerminal

            verify(exactly = 1) { toAcquirerTerminalMapper.map(preAcquirerTerminal) }
            verify(exactly = 1) { repository.create(acquirerTerminal) }
        }

        scenario("with existing terminal") {
            val preAcquirerTerminal = aPreAcquirerTerminal
            val acquirerTerminal = anAcquirerTerminal

            every { toAcquirerTerminalMapper.map(preAcquirerTerminal) } returns acquirerTerminal
            every { repository.create(acquirerTerminal) } returns acquirerTerminal

            useCase.execute(
                preAcquirerTerminal,
                Optional.of(acquirerTerminal)
            ) shouldBeLeft ApplicationError.acquirerTerminalExists()

            verify(exactly = 0) { toAcquirerTerminalMapper.map(preAcquirerTerminal) }
            verify(exactly = 0) { repository.create(acquirerTerminal) }
        }
    }
})
