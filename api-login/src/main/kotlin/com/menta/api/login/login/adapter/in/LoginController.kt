package com.menta.api.login.login.adapter.`in`

import UserCredentials
import com.menta.api.login.login.adapter.`in`.model.LoginRequest
import com.menta.api.login.shared.adapter.`in`.model.UserAuthResponse
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.login.application.port.`in`.LoginInPort
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.login.domain.mapper.ToUserCredentialsMapper
import com.menta.api.login.shared.other.util.log.CompanionLogger
import com.menta.api.login.shared.other.util.log.benchmark
import com.menta.api.login.shared.other.util.rest.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/login")
class LoginController(
    private val loginInPort: LoginInPort,
    private val toUserCredentials: ToUserCredentialsMapper,
    private val toUserAuthResponseMapper: ToUserAuthResponseMapper
) {

    @Operation(summary = "Login")
    @PostMapping
    @ResponseStatus(OK)
    fun login(@RequestBody request: LoginRequest): UserAuthResponse =
        log.benchmark("logIn requested: $request") {
            request
                .toUserCredentials()
                .doLogin()
                .throwIfLeft()
                .asResponse()
        }

    private fun LoginRequest.toUserCredentials() =
        toUserCredentials.mapFrom(this)
            .log { info("user credentials mapped: {}", it) }

    private fun UserCredentials.doLogin() =
        loginInPort.login(this)
            .logRight { info("user logged: {}", it) }

    private fun UserAuth.asResponse() =
        toUserAuthResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
