package com.menta.api.terminals.applications.port.out

import arrow.core.Either
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface TerminalRepositoryOutPort {
    fun findBy(id: UUID): Optional<Terminal>
    fun findBy(serialCode: String, tradeMark: String, model: String): Optional<Terminal>
    fun findBySerialCode(serialCode: String): Optional<Terminal>
    fun create(terminal: Terminal): Terminal
    fun update(terminal: Terminal): Either<ApplicationError, Terminal>
    fun findBy(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?,
        pageable: Pageable
    ): Page<Terminal>
}
