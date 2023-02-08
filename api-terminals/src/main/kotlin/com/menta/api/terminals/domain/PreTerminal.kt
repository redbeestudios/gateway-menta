package com.menta.api.terminals.domain

import java.util.UUID

data class PreTerminal(
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val features: List<Feature>
)
