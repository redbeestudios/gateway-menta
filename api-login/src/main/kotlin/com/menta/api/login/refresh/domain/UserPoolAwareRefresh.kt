package com.menta.api.login.refresh.domain

import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool

data class UserPoolAwareRefresh(
    val refresh: Refresh,
    val userPool: UserPool
)