package com.kiwi.api.payments.hexagonal.domain

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
    val features: List<Feature>
)
