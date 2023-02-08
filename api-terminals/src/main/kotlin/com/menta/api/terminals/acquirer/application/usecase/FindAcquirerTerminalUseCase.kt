package com.menta.api.terminals.acquirer.application.usecase

import arrow.core.Either
import com.menta.api.terminals.acquirer.application.port.`in`.FindAcquirerTerminalPortIn
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.acquirerTerminalNotFound
import com.menta.api.terminals.shared.utils.either.rightIfPresent
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindAcquirerTerminalUseCase(
    private val repository: AcquirerTerminalRepositoryOutPort
) : FindAcquirerTerminalPortIn {

    override fun execute(acquirer: String, terminalId: UUID): Either<ApplicationError, AcquirerTerminal> =
        findBy(acquirer, terminalId)
            .shouldBePresent(terminalId, acquirer)

    private fun findBy(acquirer: String, terminalId: UUID) =
        repository.findBy(acquirer, terminalId)
            .log { info("terminal {} for acquirer {} found: {}", terminalId, acquirer, it) }

    override fun find(terminalId: UUID, acquirerId: String): Optional<AcquirerTerminal> =
        repository.findBy(acquirerId, terminalId)
            .log { info("acquirer terminal found {}", it) }

    private fun Optional<AcquirerTerminal>.shouldBePresent(terminalId: UUID, acquirer: String) =
        rightIfPresent(error = acquirerTerminalNotFound(acquirer, terminalId))
            .logEither(
                { error("terminal {} for acquirer {} not found", terminalId, acquirer) },
                { info("terminal {} for acquirer {} found: {}", terminalId, acquirer, it) }
            )

    companion object : CompanionLogger()
}
