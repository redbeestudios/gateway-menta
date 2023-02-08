package com.menta.bff.devices.login.login.refresh.adapter.out.model.mapper

import com.menta.bff.devices.login.login.refresh.adapter.out.model.RefreshClientRequest
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToRefreshClientRequestMapper {

    fun mapFrom(refresh: Refresh): RefreshClientRequest =
        with(refresh) {
            RefreshClientRequest(
                refreshToken = token,
                userType = userType
            )
        }.log { info("refresh client request mapped: {}", it) }

    companion object : CompanionLogger()
}
