package com.menta.bff.devices.login.login.challenge.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface NewPasswordChallengePortOut {
    fun solve(challenge: NewPasswordChallengeSolution): Either<ApplicationError, UserAuth>
}
