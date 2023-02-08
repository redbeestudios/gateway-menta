package com.menta.bff.devices.login.orchestrate.adapter.`in`

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.ConfirmRestoreUserPasswordRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedLoginRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedNewPasswordChallengeRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRefreshRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRestorePasswordRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRevokeTokenRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratorResponse
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper.ToConfirmRestoreUserPasswordMapper
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper.ToOrchestratorResponseMapper
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedConfirmPasswordInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedLoginInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedNewPasswordChallengeInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRefreshInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRenewPasswordInPort
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRevokeTokenInPort
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedRestoreUserPassword
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedRestoreUserPasswordMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserCredentialsMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserNewPasswordChallengeMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserRefreshMapper
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedUserRevokeTokenMapper
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import com.menta.bff.devices.login.shared.other.util.log.benchmark
import com.menta.bff.devices.login.shared.other.util.rest.throwIfLeft
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class OrchestratorController(
    private val orchestratedLoginInPort: OrchestratedLoginInPort,
    private val orchestratedRefreshInPort: OrchestratedRefreshInPort,
    private val orchestratedRevokeTokenInPort: OrchestratedRevokeTokenInPort,
    private val orchestratedNewPasswordChallengeInPort: OrchestratedNewPasswordChallengeInPort,
    private val orchestratedRenewPasswordInPort: OrchestratedRenewPasswordInPort,
    private val orchestratedConfirmPasswordInPort: OrchestratedConfirmPasswordInPort,
    private val toOrchestratedUserCredentialsMapper: ToOrchestratedUserCredentialsMapper,
    private val toOrchestratedUserRefreshMapper: ToOrchestratedUserRefreshMapper,
    private val toOrchestratedUserRevokeTokenMapper: ToOrchestratedUserRevokeTokenMapper,
    private val toOrchestratedUserNewPasswordChallengeMapper: ToOrchestratedUserNewPasswordChallengeMapper,
    private val toOrchestratedRestoreUserPasswordMapper: ToOrchestratedRestoreUserPasswordMapper,
    private val toConfirmRestoreUserPasswordMapper: ToConfirmRestoreUserPasswordMapper,
    private val toResponseMapper: ToOrchestratorResponseMapper
) {

    @PostMapping("/public/bff-devices-login/login")
    fun login(@Valid @RequestBody request: OrchestratedLoginRequest): OrchestratorResponse =
        log.benchmark("orchestrated login: $request") {
            request.toOrchestratedUserCredentials()
                .doLogin()
                .throwIfLeft()
                .toResponse()
        }

    @PostMapping("/public/bff-devices-login/login/refresh")
    fun login(@Valid @RequestBody request: OrchestratedRefreshRequest): OrchestratorResponse =
        log.benchmark("orchestrated refresh: $request") {
            request.toOrchestratedUserRefresh()
                .doRefresh()
                .throwIfLeft()
                .toResponse()
        }

    @PostMapping("/public/bff-devices-login/login/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@Valid @RequestBody request: OrchestratedRevokeTokenRequest) =
        log.benchmark("orchestrated revoke: $request") {
            request.toOrchestratedUserRevokeToken()
                .doRevoke()
                .throwIfLeft()
        }

    @PostMapping("/public/bff-devices-login/login/challenge_solutions/new_password_required")
    fun login(@Valid @RequestBody request: OrchestratedNewPasswordChallengeRequest): OrchestratorResponse =
        log.benchmark("orchestrated new password challenge: $request") {
            request.toOrchestratedUserNewPasswordChallenge()
                .doSolveChallenge()
                .throwIfLeft()
                .toResponse()
        }

    @PostMapping("/public/bff-devices-login/login/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@Valid @RequestBody request: OrchestratedRestorePasswordRequest) =
        log.benchmark("orchestrated restore password: $request") {
            request.toOrchestratedUserForgotPassword()
                .doRenewPassword()
                .throwIfLeft()
        }

    @PostMapping("/public/bff-devices-login/login/confirm-forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@Valid @RequestBody request: ConfirmRestoreUserPasswordRequest) =
        log.benchmark("orchestrated restore password: $request") {
            request.toConfirmRestoreUserPassword()
                .confirm()
                .throwIfLeft()
        }

    private fun OrchestratedLoginRequest.toOrchestratedUserCredentials() =
        toOrchestratedUserCredentialsMapper.mapFrom(this)
            .log { info("orchestrated user credentials mapped: {}", it) }

    private fun OrchestratedRefreshRequest.toOrchestratedUserRefresh() =
        toOrchestratedUserRefreshMapper.mapFrom(this)
            .log { info("orchestrated user refresh token mapped: {}", it) }

    private fun OrchestratedRevokeTokenRequest.toOrchestratedUserRevokeToken() =
        toOrchestratedUserRevokeTokenMapper.mapFrom(this)
            .log { info("orchestrated user revoke token mapped: {}", it) }

    private fun OrchestratedNewPasswordChallengeRequest.toOrchestratedUserNewPasswordChallenge() =
        toOrchestratedUserNewPasswordChallengeMapper.mapFrom(this)
            .log { info("orchestrated user challenge mapped: {}", it) }

    private fun OrchestratedRestorePasswordRequest.toOrchestratedUserForgotPassword() =
        toOrchestratedRestoreUserPasswordMapper.mapFrom(this)

    private fun ConfirmRestoreUserPasswordRequest.toConfirmRestoreUserPassword() =
        toConfirmRestoreUserPasswordMapper.mapFrom(this)

    private fun OrchestratedUserCredentials.doLogin() =
        orchestratedLoginInPort.login(this)
            .logRight { info("user logged in: {}", it) }

    private fun OrchestratedUserRefresh.doRefresh() =
        orchestratedRefreshInPort.refresh(this)
            .logRight { info("user refresh token in: {}", it) }

    private fun OrchestratedUserRevokeToken.doRevoke() =
        orchestratedRevokeTokenInPort.revoke(this)
            .logRight { info("user revoke token successful") }

    private fun OrchestratedUserChallenge.doSolveChallenge() =
        orchestratedNewPasswordChallengeInPort.solve(this)
            .logRight { info("user challenge in: {}", it) }

    private fun OrchestratedRestoreUserPassword.doRenewPassword() =
        orchestratedRenewPasswordInPort.solve(this)
            .logRight { info("user password restore successfully") }

    private fun ConfirmRestoreUserPassword.confirm() =
        orchestratedConfirmPasswordInPort.confirm(this)
            .logRight { info("user password confirm successfully") }

    private fun OrchestratedAuth.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
