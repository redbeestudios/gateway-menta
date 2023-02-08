package com.menta.api.login.challenge.domain.mapper

import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToChallengeSolutionMapperSpec : FeatureSpec({

    val mapper = ToChallengeSolutionMapper()


    feature("map from challenge solution request") {

        scenario("map") {

            val request = ChallengeSolutionRequest(
                userType = MERCHANT,
                challengeName = NEW_PASSWORD_REQUIRED,
                session = "a session",
                attributes = mapOf(
                    USERNAME to "a user",
                    NEW_PASSWORD to "a new password"
                )
            )

            val solution = ChallengeSolution(
                userType = MERCHANT,
                challengeName = NEW_PASSWORD_REQUIRED,
                session = "a session",
                attributes = mapOf(
                    USERNAME to "a user",
                    NEW_PASSWORD to "a new password"
                )
            )
            mapper.mapFrom(request) shouldBe solution
        }
    }
}) {
}