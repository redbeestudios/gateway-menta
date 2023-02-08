package com.menta.api.login.shared.domain.mapper

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult
import com.menta.api.login.challenge.domain.ChallengeAttribute.PASSWORD_CLAIM_SIGNATURE
import com.menta.api.login.challenge.domain.ChallengeAttribute.SMS_MFA_CODE
import com.menta.api.login.challenge.domain.ChallengeAttribute.USERNAME
import com.menta.api.login.challenge.domain.ChallengeName.NEW_PASSWORD_REQUIRED
import com.menta.api.login.shared.domain.UserAuth
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserAuthMapperSpec : FeatureSpec({

    val mapper = ToUserAuthMapper()

    feature("map user auth from auth result") {
        scenario("map auth result with token") {

            val result = InitiateAuthResult()
                .withAuthenticationResult(
                    AuthenticationResultType()
                        .withAccessToken("an access token")
                        .withExpiresIn(3600)
                        .withTokenType("Bearer")
                        .withRefreshToken("a refresh token")
                        .withIdToken("an id token")
                )

            val userAuth = UserAuth(
                token = UserAuth.Token(
                    accessToken = "an access token",
                    expiresIn = 3600,
                    tokenType = "Bearer",
                    refreshToken = "a refresh token",
                    idToken = "an id token"
                ),
                challenge = null
            )

            mapper.mapFrom(result) shouldBe userAuth

        }

        scenario("map auth result with challenge") {

            val result = InitiateAuthResult()
                .withChallengeName("NEW_PASSWORD_REQUIRED")
                .withSession("a session")
                .withChallengeParameters(
                    mapOf(
                        "USERNAME" to "a username",
                        "SMS_MFA_CODE" to "a sms mfa code",
                        "PASSWORD_CLAIM_SIGNATURE" to "a password claim signature"
                    )
                )

            val userAuth = UserAuth(
                token = null,
                challenge = UserAuth.Challenge(
                    name = NEW_PASSWORD_REQUIRED,
                    parameters = mapOf(
                        USERNAME to "a username",
                        SMS_MFA_CODE to "a sms mfa code",
                        PASSWORD_CLAIM_SIGNATURE to "a password claim signature"
                    ),
                    session = "a session"
                )
            )

            mapper.mapFrom(result) shouldBe userAuth

        }

    }

    feature("map user auth from challenge result") {
        scenario("map auth result with token") {

            val result = RespondToAuthChallengeResult()
                .withAuthenticationResult(
                    AuthenticationResultType()
                        .withAccessToken("an access token")
                        .withExpiresIn(3600)
                        .withTokenType("Bearer")
                        .withRefreshToken("a refresh token")
                        .withIdToken("an id token")
                )

            val userAuth = UserAuth(
                token = UserAuth.Token(
                    accessToken = "an access token",
                    expiresIn = 3600,
                    tokenType = "Bearer",
                    refreshToken = "a refresh token",
                    idToken = "an id token"
                ),
                challenge = null
            )

            mapper.mapFrom(result) shouldBe userAuth

        }

        scenario("map auth result with challenge") {

            val result = RespondToAuthChallengeResult()
                .withChallengeName("NEW_PASSWORD_REQUIRED")
                .withSession("a session")
                .withChallengeParameters(
                    mapOf(
                        "USERNAME" to "a username",
                        "SMS_MFA_CODE" to "a sms mfa code",
                        "PASSWORD_CLAIM_SIGNATURE" to "a password claim signature"
                    )
                )

            val userAuth = UserAuth(
                token = null,
                challenge = UserAuth.Challenge(
                    name = NEW_PASSWORD_REQUIRED,
                    parameters = mapOf(
                        USERNAME to "a username",
                        SMS_MFA_CODE to "a sms mfa code",
                        PASSWORD_CLAIM_SIGNATURE to "a password claim signature"
                    ),
                    session = "a session"
                )
            )

            mapper.mapFrom(result) shouldBe userAuth

        }

    }

})
