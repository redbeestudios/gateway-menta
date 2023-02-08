package com.menta.api.login.refresh.domain.mapper

import com.menta.api.login.refresh.adapter.`in`.model.RefreshRequest
import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToRefreshMapper {

    fun mapFrom(request: RefreshRequest) =
        with(request) {
            Refresh(
                token = refreshToken,
                userType = userType
            )
        }.log { info("refresh mapped from request: {}", it) }

    companion object : CompanionLogger()
}