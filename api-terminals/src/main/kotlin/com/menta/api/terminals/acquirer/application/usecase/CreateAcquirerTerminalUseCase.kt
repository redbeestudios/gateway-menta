package com.menta.api.terminals.acquirer.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.applications.mapper.ToAcquirerTerminalMapper
import com.menta.api.terminals.applications.port.`in`.CreateAcquirerTerminalPortIn
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.acquirerTerminalExists
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import java.util.Optional
import org.springframework.stereotype.Component

@Component
class CreateAcquirerTerminalUseCase(
    private val acquirerTerminalRepository: AcquirerTerminalRepositoryOutPort,
    private val toAcquirerTerminalMapper: ToAcquirerTerminalMapper
) : CreateAcquirerTerminalPortIn {

    override fun execute(
        preAcquirerTerminal: PreAcquirerTerminal,
        existingAcquirerTerminal: Optional<AcquirerTerminal>
    ): Either<ApplicationError, AcquirerTerminal> =
        existingAcquirerTerminal.shouldNotExist().map {
            preAcquirerTerminal
                .toAcquirerTerminal()
                .save()
                .log { info("Acquirer terminal {} for {} created", it.terminalId.toString(), it.acquirer) }
        }

    private fun Optional<AcquirerTerminal>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            acquirerTerminalExists().left()
        }

    private fun PreAcquirerTerminal.toAcquirerTerminal(): AcquirerTerminal = toAcquirerTerminalMapper.map(this)

    private fun AcquirerTerminal.save() = acquirerTerminalRepository.create(this)

    companion object : CompanionLogger()
}
