package com.menta.bff.devices.login.orchestrate.adapter.`in`.model

import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.Valid

data class OrchestratedNewPasswordChallengeRequest(
    @Schema(description = "Session generated when challenge was proposed")
    val session: String,
    @Schema(
        type = "string",
        example = "menta@menta.com",
        description = "User wanting to solve the challenge"
    )
    val user: String,
    @Schema(description = "Type of the user")
    val userType: UserType,
    @Schema(
        type = "string",
        example = "Menta2022!",
        description = "New password for the user"
    )
    val newPassword: String,
    @Valid
    @Schema(description = "parameters to search other entities")
    val orchestratedEntities: OrchestratedEntitiesRequest?
) {
    override fun toString(): String {
        return "NewPassworddChallengeRequest(user='$user', userType=$userType, orchestratedEntities=$orchestratedEntities)"
    }
}
