package com.menta.bff.devices.login.entities.deviceFlow.domain

import java.util.UUID

data class DeviceFlow(
    val id: UUID,
    val field: String,
    val uri: String,
    val model: String,
    val flowType: String,
    val order: Int,
    val validate: Validate?
) {
    data class Validate(
        val rule: String?,
        val validations: List<Validation>
    )

    data class Validation(
        val field: String,
        val rule: String,
        val value: String
    )
}
