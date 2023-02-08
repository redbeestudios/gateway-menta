package com.menta.api.terminals.applications.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.applications.port.`in`.CreateTerminalPortIn
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.PreTerminal
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.domain.provider.IdProvider
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalExists
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreateTerminalUseCase(
    private val terminalRepository: TerminalRepositoryOutPort,
    private val idProvider: IdProvider
) : CreateTerminalPortIn {

    override fun execute(
        preTerminal: PreTerminal,
        existingTerminal: Optional<Terminal>
    ): Either<ApplicationError, Terminal> =
        existingTerminal.shouldNotExist().map {
            preTerminal
                .buildTerminal()
                .save()
                .log { info("Terminal created with id {} and serialCode {}", it.id, it.serialCode) }
        }

    private fun Optional<Terminal>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            terminalExists().left()
        }

    private fun Terminal.save() = terminalRepository.create(this)

    private fun PreTerminal.buildTerminal(): Terminal =
        Terminal(
            id = idProvider.provide(),
            merchantId = merchantId,
            customerId = customerId,
            serialCode = serialCode,
            hardwareVersion = hardwareVersion,
            tradeMark = tradeMark,
            model = model,
            features = features,
            status = Status.ACTIVE,
            deleteDate = null
        )
            .log { debug("Terminal build: {}", it) }

    companion object : CompanionLogger()
}
