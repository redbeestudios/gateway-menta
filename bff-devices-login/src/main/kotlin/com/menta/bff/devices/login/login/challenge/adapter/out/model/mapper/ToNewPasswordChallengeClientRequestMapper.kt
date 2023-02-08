package com.menta.bff.devices.login.login.challenge.adapter.out.model.mapper

import com.menta.bff.devices.login.login.challenge.adapter.out.model.NewPasswordChallengeClientRequest
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToNewPasswordChallengeClientRequestMapper {

    fun mapFrom(newPasswordChallengeSolution: NewPasswordChallengeSolution): NewPasswordChallengeClientRequest =
        with(newPasswordChallengeSolution) {
            NewPasswordChallengeClientRequest(
                session = session,
                user = user,
                userType = userType,
                newPassword = newPassword
            )
        }.log { info("challenge client request mapped: {}", it) }

    companion object : CompanionLogger()
}
