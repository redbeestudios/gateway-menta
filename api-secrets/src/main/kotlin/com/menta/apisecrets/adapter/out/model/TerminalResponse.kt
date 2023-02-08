package com.menta.apisecrets.adapter.out.model

import java.util.UUID

data class TerminalResponse(
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val status: Status,
    val features: List<Feature>
) {
    enum class Feature {
        MANUAL, STRIPE, CHIP, CONTACTLESS;
    }

    enum class Status {
        ACTIVE, INACTIVE;
    }
}
