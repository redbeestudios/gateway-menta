package com.menta.api.login.challenge.domain

import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.shared.domain.UserType

data class ChallengeSolution(
    val userType: UserType,
    val challengeName: ChallengeName,
    val session: String,
    val attributes: Map<ChallengeAttribute, String>
) {
    override fun toString(): String {
        return "ChallengeSolution(userType=$userType, challengeName=$challengeName, attributes=${attributes.filter { it.key != NEW_PASSWORD }})"
    }
}
