package com.menta.api.terminals.adapter.`in`.model

import com.menta.api.terminals.domain.Feature
import java.util.UUID

data class TerminalRequest(
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val features: List<Feature>
)
