package com.menta.bff.devices.login.login.auth.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.auth.adapter.out.model.mapper.ToLoginClientRequestMapper
import com.menta.bff.devices.login.login.auth.application.port.out.LoginPortOut
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserCredentials
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
class LoginClient(
    private val webClient: WebClient,
    @Value("\${externals.login.auth.url}")
    private val url: String,
    @Value("\${externals.login.auth.request-timeout}")
    private val timeout: Long,
    private val toLoginClientRequestMapper: ToLoginClientRequestMapper
) : LoginPortOut {

    override fun login(credentials: UserCredentials): Either<ApplicationError, UserAuth> =
        log.benchmark("login") {
            try {
                webClient.post()
                    .uri(URI(url))
                    .bodyValue(credentials.toRequest())
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
            { error("login failed: {}", it) },
            { info("user logged in: {}", it) }
        )

    private fun UserCredentials.toRequest() =
        toLoginClientRequestMapper.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun Mono<UserAuth>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while trying to log the user into the Login Api"))
        }

    companion object : CompanionLogger()
}
