package com.menta.api.credibanco.domain.field

data class TerminalData(
    val ownerFiid: String,
    val logicalNetwork: String,
    val timeOffset: String,
    val terminalId: String
)
