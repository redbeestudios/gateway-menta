package com.menta.bff.devices.login.login.challenge.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.challenge.adapter.out.model.mapper.ToNewPasswordChallengeClientRequestMapper
import com.menta.bff.devices.login.login.challenge.application.port.out.NewPasswordChallengePortOut
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.exception.ApplicationErrorException
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import com.menta.bff.devices.login.shared.other.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class NewPasswordChallengeClient(
    private val webClient: WebClient,
    @Value("\${externals.login.challenge.url}")
    private val url: String,
    @Value("\${externals.login.auth.request-timeout}")
    private val timeout: Long,
    private val toNewPasswordChallengeClientRequestMapper: ToNewPasswordChallengeClientRequestMapper
) : NewPasswordChallengePortOut {

    override fun solve(challenge: NewPasswordChallengeSolution): Either<ApplicationError, UserAuth> =
        log.benchmark("new password challenge") {
            try {
                webClient.post()
                    .uri(URI(url))
                    .bodyValue(challenge.toRequest())
                    .retrieve()
                    .bodyToMono(UserAuth::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()!!
                    .right()
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }
        }.logEither(
            { error("challenge user failed: {}", it) },
            { info("challenge user in: {}", it) }
        )

    private fun NewPasswordChallengeSolution.toRequest() =
        toNewPasswordChallengeClientRequestMapper.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun Mono<UserAuth>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while trying to solve challenge into the Login Api"))
        }

    companion object : CompanionLogger()
}
