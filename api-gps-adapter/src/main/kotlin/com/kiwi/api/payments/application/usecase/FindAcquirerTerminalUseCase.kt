package com.kiwi.api.payments.application.usecase

import com.kiwi.api.payments.application.port.`in`.FindAcquirerTerminalInPort
import com.kiwi.api.payments.application.port.out.AcquirerTerminalRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerTerminal
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirerTerminalUseCase(
    private val repository: AcquirerTerminalRepositoryOutPort
) : FindAcquirerTerminalInPort {

    override fun execute(terminalId: UUID): AcquirerTerminal =
        log.benchmark("Find Acquirer Terminal with id: $terminalId") {
            repository.findBy(terminalId)
                .log { info("Acquirer Terminal found: {}", it) }
        }

    companion object : CompanionLogger()
}
