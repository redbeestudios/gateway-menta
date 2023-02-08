package com.menta.api.login.challenge.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult
import com.menta.api.login.challenge.adapter.out.mapper.ToRespondToAuthChallengeRequestMapper
import com.menta.api.login.challenge.application.port.out.ChallengeSolutionOutPort
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.domain.mapper.ToUserAuthMapper
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.handleCognitoError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import com.menta.api.login.shared.other.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class CognitoChallengeSolutionClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toRequestMapper: ToRespondToAuthChallengeRequestMapper,
    private val toUserAuthMapper: ToUserAuthMapper
) : ChallengeSolutionOutPort {

    override fun solve(solution: UserPoolAwareChallengeSolution): Either<ApplicationError, UserAuth> =
        log.benchmark("solve challenge $solution") {
            try {
                solution
                    .toRequest()
                    .doSolve()
                    .toChallengeResult()
                    .right()
            } catch (e: Throwable) {
                e.handleCognitoError().left()
            }
        }

    private fun UserPoolAwareChallengeSolution.toRequest() =
        toRequestMapper.mapFrom(this)

    private fun RespondToAuthChallengeRequest.doSolve() =
        awsCognitoIdentityProvider.respondToAuthChallenge(this)
            .log { info("solve result: {}", it) }

    private fun RespondToAuthChallengeResult.toChallengeResult() =
        toUserAuthMapper.mapFrom(this)
            .log { info("challenge result mapped: {}", it) }

    companion object : CompanionLogger()

}
