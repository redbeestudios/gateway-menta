package com.kiwi.api.reversal.hexagonal.application.usecase.entities

import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reversal.hexagonal.application.port.out.TerminalRepositoryPortOut
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindTerminalUseCase(
    private val terminalRepository: TerminalRepositoryPortOut,
) : FindTerminalPortIn {

    override fun execute(terminalId: UUID): ReceivedTerminal =
        terminalRepository.retrieve(terminalId)
}
