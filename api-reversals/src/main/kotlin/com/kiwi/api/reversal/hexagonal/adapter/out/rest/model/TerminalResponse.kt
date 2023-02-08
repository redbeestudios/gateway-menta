package com.kiwi.api.reversal.hexagonal.adapter.out.rest.model

import com.kiwi.api.reversal.hexagonal.domain.entities.Feature
import java.util.UUID

data class TerminalResponse(
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val status: String,
    val features: List<Feature>
)
