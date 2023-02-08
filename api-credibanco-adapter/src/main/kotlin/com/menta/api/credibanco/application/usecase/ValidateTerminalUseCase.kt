package com.menta.api.credibanco.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.credibanco.application.port.`in`.ValidateTerminalPortIn
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.domain.Terminal
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.OutdatedTerminal
import org.springframework.stereotype.Component

@Component
class ValidateTerminalUseCase(
    val terminalsRepository: TerminalsRepository
) : ValidateTerminalPortIn {

    override fun execute(terminal: Terminal): Either<ApplicationError, Boolean> =
        when (terminalsRepository.exists("${terminal.id}")) {
            true -> true.right()
            false -> OutdatedTerminal(terminal.serialCode).left()
        }
}
