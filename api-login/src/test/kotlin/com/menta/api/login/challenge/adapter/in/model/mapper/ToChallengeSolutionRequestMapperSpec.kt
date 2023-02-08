package com.menta.api.login.challenge.adapter.`in`.model.mapper

import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.adapter.`in`.model.NewPasswordRequiredChallengeRequest
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToChallengeSolutionRequestMapperSpec : FeatureSpec({

    feature("map challenge solution from new password required challenge") {

        val mapper = ToChallengeSolutionRequestMapper()

        scenario("map") {

            val newPasswordSolution = NewPasswordRequiredChallengeRequest(
                session = "a session",
                user = "a user",
                userType = MERCHANT,
                newPassword = "a new password"
            )

            val challengeSolutionRequest = ChallengeSolutionRequest(
                userType = MERCHANT,
                challengeName = NEW_PASSWORD_REQUIRED,
                session = "a session",
                attributes = mapOf(USERNAME to "a user", NEW_PASSWORD to "a new password")
            )

            mapper.mapFrom(newPasswordSolution) shouldBe challengeSolutionRequest
        }
    }
})