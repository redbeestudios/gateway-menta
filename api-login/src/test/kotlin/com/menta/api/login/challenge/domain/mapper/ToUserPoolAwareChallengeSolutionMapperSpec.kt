package com.menta.api.login.challenge.domain.mapper

import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.domain.UserType.MERCHANT
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserPoolAwareChallengeSolutionMapperSpec : FeatureSpec({

    val mapper = ToUserPoolAwareChallengeSolutionMapper()

    feature("map user pool aware challenge solution from user pool and solution") {

        scenario("map") {
            val userPool = CognitoConfigurationProperties.Provider.UserPool(
                code = "a user pool code",
                clientId = "a user pool client id"
            )

            val solution = ChallengeSolution(
                userType = MERCHANT,
                challengeName = NEW_PASSWORD_REQUIRED,
                session = "a session",
                attributes = mapOf(
                    ChallengeAttribute.USERNAME to "a user",
                    ChallengeAttribute.NEW_PASSWORD to "a new password"
                )
            )

            mapper.mapFrom(userPool, solution) shouldBe
                    UserPoolAwareChallengeSolution(
                        userPool = userPool,
                        solution = solution
                    )

        }
    }
})