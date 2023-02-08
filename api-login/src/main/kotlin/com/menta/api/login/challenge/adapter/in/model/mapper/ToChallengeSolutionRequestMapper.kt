package com.menta.api.login.challenge.adapter.`in`.model.mapper

import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.adapter.`in`.model.NewPasswordRequiredChallengeRequest
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToChallengeSolutionRequestMapper {

    fun mapFrom(newPasswordRequiredChallengeRequest: NewPasswordRequiredChallengeRequest) =
        with(newPasswordRequiredChallengeRequest) {
            ChallengeSolutionRequest(
                userType = userType,
                challengeName = NEW_PASSWORD_REQUIRED,
                session = session,
                attributes = mapOf(
                    USERNAME to user,
                    NEW_PASSWORD to newPassword
                )
            )
        }.log { info("new password required challenge solution mapped: {}", it) }

    companion object : CompanionLogger()
}