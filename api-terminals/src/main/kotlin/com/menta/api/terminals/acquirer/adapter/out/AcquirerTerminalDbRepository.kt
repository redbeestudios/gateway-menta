package com.menta.api.terminals.acquirer.adapter.out

import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional
import java.util.UUID

interface AcquirerTerminalDbRepository : MongoRepository<AcquirerTerminal, UUID> {
    fun findByAcquirerAndTerminalId(acquirer: String, terminalId: UUID): Optional<AcquirerTerminal>
}
