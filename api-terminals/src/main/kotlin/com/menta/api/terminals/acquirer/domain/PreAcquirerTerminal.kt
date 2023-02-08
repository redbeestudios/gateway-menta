package com.menta.api.terminals.acquirer.domain

import java.util.UUID

data class PreAcquirerTerminal(
    val terminalId: UUID,
    val acquirerId: String,
    val code: String
)