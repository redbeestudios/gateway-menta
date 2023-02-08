package com.menta.api.credibanco.application.usecase

import arrow.core.Either
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import com.menta.api.credibanco.application.port.`in`.RegisterUpdatedTerminalPortIn
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.TerminalAlreadyRegistered
import org.springframework.stereotype.Component

@Component
class RegisterUpdatedTerminalUseCase(
    val terminalsRepository: TerminalsRepository
) : RegisterUpdatedTerminalPortIn {

    override fun execute(terminalId: String): Either<ApplicationError, String> =
        when (terminalsRepository.register(terminalId)) {
            is Some -> terminalId.right()
            else -> TerminalAlreadyRegistered(terminalId).left()
        }
}
