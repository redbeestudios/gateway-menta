package com.menta.api.terminals.acquirer.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.applications.port.`in`.UpdateAcquirerTerminalPortIn
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.acquirerTerminalDoesNotExists
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import java.util.Optional
import org.springframework.stereotype.Component

@Component
class UpdateAcquirerTerminalUseCase(
    private val acquirerTerminalRepository: AcquirerTerminalRepositoryOutPort
) : UpdateAcquirerTerminalPortIn {

    override fun execute(
        preAcquirerTerminal: PreAcquirerTerminal,
        existingAcquirerTerminal: Optional<AcquirerTerminal>
    ): Either<ApplicationError, AcquirerTerminal> =
        existingAcquirerTerminal.shouldExist().flatMap {
            it.copy(code = preAcquirerTerminal.code)
                .update()
                .log { info("Acquirer terminal updated") }
        }

    private fun Optional<AcquirerTerminal>.shouldExist() =
        if (this.isEmpty) {
            acquirerTerminalDoesNotExists().left()
        } else {
            this.get().right()
        }

    private fun AcquirerTerminal.update() = acquirerTerminalRepository.update(this)

    companion object : CompanionLogger()
}
