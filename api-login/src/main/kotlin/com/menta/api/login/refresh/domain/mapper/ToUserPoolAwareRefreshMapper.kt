package com.menta.api.login.refresh.domain.mapper

import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserPoolAwareRefreshMapper {

    fun mapFrom(refresh: Refresh, userPool: UserPool) =
        UserPoolAwareRefresh(
            refresh = refresh,
            userPool = userPool
        ).log { info("user pool aware refresh mapped: {}", it) }

    companion object : CompanionLogger()
}