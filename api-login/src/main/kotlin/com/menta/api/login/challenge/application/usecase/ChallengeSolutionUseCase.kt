package com.menta.api.login.challenge.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.login.challenge.application.port.`in`.ChallengeSolutionInPort
import com.menta.api.login.challenge.application.port.out.ChallengeSolutionOutPort
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.challenge.domain.mapper.ToUserPoolAwareChallengeSolutionMapper
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ChallengeSolutionUseCase(
    private val challengeSolutionOutPort: ChallengeSolutionOutPort,
    private val userPoolProvider: UserPoolProvider,
    private val toUserPoolAwareChallengeSolution: ToUserPoolAwareChallengeSolutionMapper
) : ChallengeSolutionInPort {

    override fun solveChallenge(solution: ChallengeSolution): Either<ApplicationError, UserAuth> =
        solution.withUserPool().flatMap {
            it.solve()
        }

    private fun ChallengeSolution.withUserPool() =
        userPoolProvider.provideFor(userType).map {
            toUserPoolAwareChallengeSolution.mapFrom(it, this)
        }
            .logRight { info("user pool added to solution: {}", it) }

    private fun UserPoolAwareChallengeSolution.solve() =
        challengeSolutionOutPort.solve(this)
            .logRight { info("challenge solved: {}", it) }

    companion object : CompanionLogger()

}