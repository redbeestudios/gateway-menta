package com.menta.api.login.challenge.application.port.`in`

import arrow.core.Either
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError

interface ChallengeSolutionInPort {
    fun solveChallenge(solution: ChallengeSolution): Either<ApplicationError, UserAuth>
}