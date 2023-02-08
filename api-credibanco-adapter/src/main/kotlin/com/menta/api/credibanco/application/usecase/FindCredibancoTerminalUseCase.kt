package com.menta.api.credibanco.application.usecase

import com.menta.api.credibanco.application.port.`in`.FindCredibancoTerminalPortIn
import com.menta.api.credibanco.application.port.out.CredibancoTerminalRepositoryPortOut
import com.menta.api.credibanco.domain.CredibancoTerminal
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import com.menta.api.credibanco.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindCredibancoTerminalUseCase(
    private val repository: CredibancoTerminalRepositoryPortOut
) : FindCredibancoTerminalPortIn {

    override fun execute(terminalId: UUID): CredibancoTerminal =
        log.benchmark("Find Credibanco Terminal with id: $terminalId") {
            repository.findBy(terminalId)
                .log { info("Credibanco Terminal found: {}", it) }
        }
    companion object : CompanionLogger()
}
