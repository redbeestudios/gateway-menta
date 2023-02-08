package com.menta.api.terminals.domain.resolver

import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class TerminalDeletionResolver {

    fun resolveDeletion(terminal: Terminal) =
        terminal.copy(deleteDate = OffsetDateTime.now())
            .log { info("Terminal deleted: {}", it) }

    companion object : CompanionLogger()
}
