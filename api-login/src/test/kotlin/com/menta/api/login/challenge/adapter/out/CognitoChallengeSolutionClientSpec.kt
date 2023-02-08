package com.menta.api.login.challenge.adapter.out

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult
import com.menta.api.login.aUserAuthWithChallenge
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.challenge.aUserPoolAwareChallengeSolution
import com.menta.api.login.challenge.adapter.out.mapper.ToRespondToAuthChallengeRequestMapper
import com.menta.api.login.exceptionToApplicationErrorTable
import com.menta.api.login.shared.domain.mapper.ToUserAuthMapper
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CognitoChallengeSolutionClientSpec : FeatureSpec({

    val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
    val toRequestMapper = mockk<ToRespondToAuthChallengeRequestMapper>()
    val toUserAuthMapper = mockk<ToUserAuthMapper>()

    val client = CognitoChallengeSolutionClient(
        awsCognitoIdentityProvider = awsCognitoIdentityProvider,
        toRequestMapper = toRequestMapper,
        toUserAuthMapper = toUserAuthMapper
    )

    beforeEach { clearAllMocks() }

    feature("solve challenge") {

        scenario("challenge solved") {
            val userPoolAwareChallengeSolution = aUserPoolAwareChallengeSolution()
            val request = mockk<RespondToAuthChallengeRequest>()
            val result = mockk<RespondToAuthChallengeResult>()
            val userAuth = aUserAuthWithToken()

            every { toRequestMapper.mapFrom(userPoolAwareChallengeSolution) } returns request
            every { awsCognitoIdentityProvider.respondToAuthChallenge(request) } returns result
            every { toUserAuthMapper.mapFrom(result) } returns userAuth

            client.solve(userPoolAwareChallengeSolution) shouldBeRight userAuth

            verify(exactly = 1) { toRequestMapper.mapFrom(userPoolAwareChallengeSolution) }
            verify(exactly = 1) { awsCognitoIdentityProvider.respondToAuthChallenge(request) }
            verify(exactly = 1) { toUserAuthMapper.mapFrom(result) }

        }

        scenario("challenge solved, returns new challenge") {
            val userPoolAwareChallengeSolution = aUserPoolAwareChallengeSolution()
            val request = mockk<RespondToAuthChallengeRequest>()
            val result = mockk<RespondToAuthChallengeResult>()
            val userAuth = aUserAuthWithChallenge()

            every { toRequestMapper.mapFrom(userPoolAwareChallengeSolution) } returns request
            every { awsCognitoIdentityProvider.respondToAuthChallenge(request) } returns result
            every { toUserAuthMapper.mapFrom(result) } returns userAuth

            client.solve(userPoolAwareChallengeSolution) shouldBeRight userAuth

            verify(exactly = 1) { toRequestMapper.mapFrom(userPoolAwareChallengeSolution) }
            verify(exactly = 1) { awsCognitoIdentityProvider.respondToAuthChallenge(request) }
            verify(exactly = 1) { toUserAuthMapper.mapFrom(result) }

        }

        scenario("challenge returns error") {
            forAll(exceptionToApplicationErrorTable) { exception, applicationError ->

                val userPoolAwareChallengeSolution = aUserPoolAwareChallengeSolution()
                val request = mockk<RespondToAuthChallengeRequest>()

                every { toRequestMapper.mapFrom(userPoolAwareChallengeSolution) } returns request
                every { awsCognitoIdentityProvider.respondToAuthChallenge(request) } throws exception

                client.solve(userPoolAwareChallengeSolution) shouldBeLeft applicationError

            }
        }


    }

})