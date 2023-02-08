package com.menta.api.terminals.acquirer

import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalResponse
import com.menta.api.terminals.acquirer.domain.Acquirer
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquireId

const val anAcquirerId = "GPS"

val anAcquirerTerminal = AcquirerTerminal(
    id = acquireId,
    terminalId = aTerminalId,
    acquirer = anAcquirerId,
    code = "23456789"
)

val anAcquirerTerminalResponse = AcquirerTerminalResponse(
    terminalId = aTerminalId,
    acquirer = anAcquirerId,
    code = "23456789"
)

val anAcquirer = Acquirer(
    id = anAcquirerId
)
