package com.menta.api.login.challenge.domain

import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool

data class UserPoolAwareChallengeSolution(
    val userPool: UserPool,
    val solution: ChallengeSolution
)