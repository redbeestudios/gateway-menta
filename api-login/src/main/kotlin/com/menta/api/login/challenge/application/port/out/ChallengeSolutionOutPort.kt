package com.menta.api.login.challenge.application.port.out

import arrow.core.Either
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError

interface ChallengeSolutionOutPort {
    fun solve(solution: UserPoolAwareChallengeSolution): Either<ApplicationError, UserAuth>
}