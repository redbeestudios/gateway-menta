package com.menta.api.terminals.acquirer.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.acquirer.application.port.out.AcquirerTerminalRepositoryOutPort
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import com.menta.api.terminals.shared.utils.logs.benchmark
import com.menta.api.terminals.shared.utils.logs.exception
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class AcquirerTerminalRepository(
    private val dbRepository: AcquirerTerminalDbRepository
) : AcquirerTerminalRepositoryOutPort {

    override fun findBy(acquirer: String, terminalId: UUID): Optional<AcquirerTerminal> =
        log.benchmark("find acquirer terminal by id") {
            dbRepository.findByAcquirerAndTerminalId(acquirer, terminalId)
        }.log { info("searched for acquirer terminal: {}", it) }

    override fun create(acquirerTerminal: AcquirerTerminal): AcquirerTerminal =
        log.benchmark("acquirer terminal create") {
            dbRepository.insert(acquirerTerminal)
        }.log { info("acquirer terminal created: {}", it) }

    override fun update(acquirerTerminal: AcquirerTerminal): Either<ApplicationError, AcquirerTerminal> =
        try {
            log.benchmark("acquirer terminal update") {
                dbRepository.save(acquirerTerminal).right()
            }.log { info("acquirer terminal updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    companion object : CompanionLogger()
}
