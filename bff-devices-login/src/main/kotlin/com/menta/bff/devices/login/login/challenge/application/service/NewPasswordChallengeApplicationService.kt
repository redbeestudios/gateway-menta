package com.menta.bff.devices.login.login.challenge.application.service

import arrow.core.Either
import com.menta.bff.devices.login.login.challenge.application.port.out.NewPasswordChallengePortOut
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class NewPasswordChallengeApplicationService(
    private val newPasswordChallengePortOut: NewPasswordChallengePortOut
) {

    fun solve(challenge: NewPasswordChallengeSolution): Either<ApplicationError, UserAuth> =
        newPasswordChallengePortOut.solve(challenge)
            .logRight { info("user challenge in: {}", it) }

    companion object : CompanionLogger()
}
