package com.menta.api.login.challenge.adapter.out.mapper

import com.amazonaws.services.cognitoidp.model.ChallengeNameType
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest
import com.menta.api.login.challenge.domain.ChallengeAttribute.NEW_PASSWORD
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.domain.UserType.MERCHANT
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToRespondToAuthChallengeRequestMapperSpec : FeatureSpec({

    val mapper = ToRespondToAuthChallengeRequestMapper()

    feature("map respond to auth challenge request from user pool aware solution") {

        scenario("map") {
            val userPoolAwareChallengeSolution =
                UserPoolAwareChallengeSolution(
                    userPool = UserPool(
                        code = "us-east-1_Uu17TuYdO",
                        clientId = "6sfhacp44g09cn5jc710m6ags5"
                    ),
                    solution = ChallengeSolution(
                        userType = MERCHANT,
                        challengeName = NEW_PASSWORD_REQUIRED,
                        session = "a session",
                        attributes = mapOf(
                            USERNAME to "user@user.com",
                            NEW_PASSWORD to "new password"
                        )
                    )
                )

            val request = RespondToAuthChallengeRequest()
                .withClientId("6sfhacp44g09cn5jc710m6ags5")
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withSession("a session")
                .withChallengeResponses(
                    mapOf("USERNAME" to "user@user.com", "NEW_PASSWORD" to "new password")
                )

            mapper.mapFrom(userPoolAwareChallengeSolution) shouldBe request
        }

    }
})