package com.kiwi.api.reimbursements.hexagonal.application.usecase

import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.out.TerminalRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindTerminalUseCase(
    private val terminalRepository: TerminalRepositoryPortOut,
) : FindTerminalPortIn {

    override fun execute(terminalId: UUID): Terminal =
        terminalRepository.retrieve(terminalId)
}
