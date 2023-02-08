package com.menta.api.terminals.applications.usecase

import arrow.core.Either
import com.menta.api.terminals.applications.port.`in`.DeleteTerminalPortIn
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.domain.resolver.TerminalDeletionResolver
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteTerminalUseCase(
    private val terminalRepository: TerminalRepositoryOutPort,
    private val deletionResolver: TerminalDeletionResolver
) : DeleteTerminalPortIn {

    override fun execute(terminal: Terminal): Either<ApplicationError, Terminal> =
        terminal.delete().persist()

    private fun Terminal.delete() =
        deletionResolver.resolveDeletion(this)

    private fun Terminal.persist() =
        terminalRepository.update(this)

    companion object : CompanionLogger()
}
