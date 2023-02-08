package com.menta.api.terminals.acquirer.adapter.`in`.model

import java.util.UUID

data class AcquirerTerminalResponse(
    val terminalId: UUID,
    val acquirer: String,
    val code: String
)
