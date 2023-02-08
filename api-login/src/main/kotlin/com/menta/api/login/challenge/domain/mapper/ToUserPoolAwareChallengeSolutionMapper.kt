package com.menta.api.login.challenge.domain.mapper

import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.UserPoolAwareChallengeSolution
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserPoolAwareChallengeSolutionMapper {

    fun mapFrom(userPool: UserPool, solution: ChallengeSolution) =
        UserPoolAwareChallengeSolution(
            userPool, solution
        )
            .log { info("UserPoolAwareChallengeSolution mapped: {}", it) }

    companion object : CompanionLogger()
}