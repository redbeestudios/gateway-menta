package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model

import com.kiwi.api.reimbursements.hexagonal.domain.Feature
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
