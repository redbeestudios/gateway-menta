package com.menta.api.login.refresh.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.login.refresh.application.port.`in`.RefreshInPort
import com.menta.api.login.refresh.application.port.`in`.RefreshOutPort
import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.refresh.domain.UserPoolAwareRefresh
import com.menta.api.login.refresh.domain.mapper.ToUserPoolAwareRefreshMapper
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class RefreshUseCase(
    private val userPoolProvider: UserPoolProvider,
    private val toUserPoolAwareRefreshMapper: ToUserPoolAwareRefreshMapper,
    private val refreshOutPort: RefreshOutPort
) : RefreshInPort {

    override fun refresh(refresh: Refresh): Either<ApplicationError, UserAuth> =
        refresh.withUserPool().flatMap {
            it.doRefresh()
        }

    private fun Refresh.withUserPool() =
        userPoolProvider.provideFor(userType).map {
            toUserPoolAwareRefreshMapper.mapFrom(this, it)
        }
            .logRight { info("user pool added to refresh: {}", it) }

    private fun UserPoolAwareRefresh.doRefresh() =
        refreshOutPort.refresh(this)
            .logRight { info("token refreshed: {}", it) }

    companion object : CompanionLogger()
}