package com.menta.api.login.challenge.adapter.`in`

import com.menta.api.login.shared.adapter.`in`.model.UserAuthResponse
import com.menta.api.login.shared.adapter.`in`.model.mapper.ToUserAuthResponseMapper
import com.menta.api.login.challenge.adapter.`in`.model.ChallengeSolutionRequest
import com.menta.api.login.challenge.adapter.`in`.model.NewPasswordRequiredChallengeRequest
import com.menta.api.login.challenge.adapter.`in`.model.mapper.ToChallengeSolutionRequestMapper
import com.menta.api.login.challenge.application.port.`in`.ChallengeSolutionInPort
import com.menta.api.login.challenge.domain.ChallengeSolution
import com.menta.api.login.challenge.domain.mapper.ToChallengeSolutionMapper
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.util.log.CompanionLogger
import com.menta.api.login.shared.other.util.rest.throwIfLeft
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChallengeSolutionController(
    private val challengeSolutionInPort: ChallengeSolutionInPort,
    private val toChallengeSolutionMapper: ToChallengeSolutionMapper,
    private val toUserAuthResponseMapper: ToUserAuthResponseMapper,
    private val toChallengeSolutionRequestMapper: ToChallengeSolutionRequestMapper
) {

    @PostMapping("/private/login/challenge_solutions")
    fun solveChallenge(@RequestBody request: ChallengeSolutionRequest): UserAuthResponse =
        request
            .toDomain()
            .doSolve()
            .throwIfLeft()
            .asResponse()

    @PostMapping("/public/login/challenge_solutions/new_password_required")
    fun solveNewPasswordRequiredChallenge(@RequestBody request: NewPasswordRequiredChallengeRequest): UserAuthResponse =
        solveChallenge(request.asChallengeRequest())

    private fun ChallengeSolutionRequest.toDomain() =
        toChallengeSolutionMapper.mapFrom(this)
            .log { info("domain mapped: {}", it) }

    private fun ChallengeSolution.doSolve() =
        challengeSolutionInPort.solveChallenge(this)
            .logRight { info("challenge solved: {}", it) }

    private fun UserAuth.asResponse() =
        toUserAuthResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    private fun NewPasswordRequiredChallengeRequest.asChallengeRequest() =
        toChallengeSolutionRequestMapper.mapFrom(this)
            .log { info("new password required as challenge: {}", this) }

    companion object : CompanionLogger()
}


