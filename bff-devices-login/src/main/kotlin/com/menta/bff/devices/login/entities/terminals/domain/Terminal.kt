package com.menta.bff.devices.login.entities.terminals.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Terminal(
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val status: String,
    val deleteDate: OffsetDateTime?,
    val features: List<Feature>
) {

    enum class Feature {
        MANUAL, STRIPE, CHIP, CONTACTLESS;
    }
}
