package com.menta.api.login.refresh.adapter.`in`

import com.menta.api.login.refresh.adapter.`in`.model.RefreshRequest
import com.menta.api.login.refresh.application.port.`in`.RefreshInPort
import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.refresh.domain.mapper.ToRefreshMapper
import com.menta.api.login.shared.adapter.`in`.model.UserAuthResponse
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.util.log.CompanionLogger
import com.menta.api.login.shared.other.util.rest.throwIfLeft
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RefreshController(
    private val refreshInPort: RefreshInPort,
    private val toRefreshMapper: ToRefreshMapper,
    private val toUserAuthResponseMapper: ToUserAuthResponseMapper
) {

    @PostMapping("/public/login/refresh")
    fun refresh(@RequestBody request: RefreshRequest): UserAuthResponse =
        request
            .toDomain()
            .doRefresh()
            .throwIfLeft()
            .asResponse()

    private fun RefreshRequest.toDomain() =
        toRefreshMapper.mapFrom(this)
            .log { info("domain mapped: {}", it) }

    private fun Refresh.doRefresh() =
        refreshInPort.refresh(this)
            .logRight { info("token refreshed: {}", it) }

    private fun UserAuth.asResponse() =
        toUserAuthResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }


    companion object : CompanionLogger()

}