package com.menta.api.terminals.applications.usecase

import arrow.core.right
import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalDelete
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.resolver.TerminalDeletionResolver
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteTerminalUseCaseSpec : FeatureSpec({

    val deletionResolver = mockk<TerminalDeletionResolver>()
    val repository = mockk<TerminalRepositoryOutPort>()
    val useCase = DeleteTerminalUseCase(repository, deletionResolver)

    beforeEach { clearAllMocks() }

    feature("delete terminal") {

        scenario("with terminal NOT deleted") {
            val terminal = aTerminal

            every { deletionResolver.resolveDeletion(terminal) } returns aTerminalDelete
            every { repository.update(aTerminalDelete) } returns aTerminalDelete.right()

            useCase.execute(terminal) shouldBeRight aTerminalDelete

            verify(exactly = 1) { deletionResolver.resolveDeletion(terminal) }
            verify(exactly = 1) { repository.update(aTerminalDelete) }
        }
    }
})
