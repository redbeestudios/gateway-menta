package com.menta.api.terminals.applications.port.`in`

import arrow.core.Either
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import java.util.UUID

interface FindTerminalByFilterPortIn {
    fun execute(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?,
        page: Int,
        size: Int
    ): Either<ApplicationError, Page<Terminal>>
}
