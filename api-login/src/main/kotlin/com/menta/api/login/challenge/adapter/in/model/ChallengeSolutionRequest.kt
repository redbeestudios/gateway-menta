package com.menta.api.login.challenge.adapter.`in`.model

import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName
import com.menta.api.login.shared.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema

data class ChallengeSolutionRequest(
    @Schema(description = "Type of the user")
    val userType: UserType,

    @Schema(description = "Name of the challenge to solve")
    val challengeName: ChallengeName,

    @Schema(description = "Session generated when challenge was proposed")
    val session: String,

    @Schema(description = "parameters needed to solve the challenge")
    val attributes: Map<ChallengeAttribute, String>
) {
    override fun toString(): String {
        return "ChallengeSolutionRequest(userType=$userType, challengeName=$challengeName)"
    }
}