package com.menta.api.terminals.applications.usecase

import arrow.core.Either
import com.menta.api.terminals.applications.port.`in`.FindTerminalPortIn
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalNotFound
import com.menta.api.terminals.shared.utils.either.rightIfPresent
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class FindTerminalUseCase(
    private val terminalRepository: TerminalRepositoryOutPort
) : FindTerminalPortIn {
    override fun execute(terminalId: UUID): Either<ApplicationError, Terminal> =
        findBy(terminalId)
            .shouldBePresent(terminalId)

    override fun findByUnivocity(serialCode: String, tradeMark: String, model: String): Optional<Terminal> =
        terminalRepository.findBy(serialCode, tradeMark, model)
            .log { info("terminal found {}", it) }

    private fun findBy(terminalId: UUID) =
        terminalRepository.findBy(terminalId)

    private fun Optional<Terminal>.shouldBePresent(terminalId: UUID) =
        rightIfPresent(error = terminalNotFound(terminalId))
            .logEither(
                { error("terminal {} not found", terminalId.toString()) },
                { debug("terminal found: {}", it) }
            )

    companion object : CompanionLogger()
}
