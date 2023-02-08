package com.menta.api.login.shared.domain.mapper

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult
import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserAuthMapper {

    fun mapFrom(initiateAuthResult: InitiateAuthResult): UserAuth =
        with(initiateAuthResult) {
            UserAuth(
                token = authenticationResult?.map(),
                challenge = challengeName?.let {
                    mapChallenge(
                        name = challengeName,
                        session = session,
                        parameters = challengeParameters
                    )
                }
            )
        }.log { info("userAuth mapped: {}", it) }

    fun mapFrom(respondToAuthChallengeResult: RespondToAuthChallengeResult) =
        with(respondToAuthChallengeResult) {
            UserAuth(
                token = authenticationResult?.map(),
                challenge = challengeName?.let {
                    mapChallenge(
                        name = challengeName,
                        session = session,
                        parameters = challengeParameters
                    )
                }

            )
        }.log { info("userAuth mapped: {}", it) }

    private fun AuthenticationResultType.map() =
        UserAuth.Token(
            accessToken = accessToken,
            expiresIn = expiresIn,
            tokenType = tokenType,
            refreshToken = refreshToken,
            idToken = idToken
        )

    private fun mapChallenge(session: String, name: String, parameters: Map<String, String>) =
        UserAuth.Challenge(
            session = session,
            name = ChallengeName.valueOf(name),
            parameters = parameters
                .filter { !IGNORED_PARAMETERS.contains(it.key) }
                .mapKeys { ChallengeAttribute.valueOf(it.key) }
        )

    companion object : CompanionLogger() {
        private val IGNORED_PARAMETERS = listOf("userAttributes", "requiredAttributes")
    }
}
