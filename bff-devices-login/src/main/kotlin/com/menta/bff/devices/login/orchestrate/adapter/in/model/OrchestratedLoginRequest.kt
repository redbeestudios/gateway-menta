package com.menta.bff.devices.login.orchestrate.adapter.`in`.model

import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.Valid

data class OrchestratedLoginRequest(
    @Schema(type = "string", example = "mail@menta.global")
    val user: Email,
    @Schema(type = "string", example = "Menta2022")
    val password: String,
    val userType: UserType = MERCHANT,
    @Valid
    @Schema(description = "parameters to search other entities")
    val orchestratedEntities: OrchestratedEntitiesRequest?
) {
    override fun toString(): String {
        return "LoginRequest(user='$user', userType=$userType, orchestratedEntities=$orchestratedEntities)"
    }
}
