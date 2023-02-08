package com.menta.bff.devices.login.login.refresh.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.refresh.adapter.out.model.mapper.ToRefreshClientRequestMapper
import com.menta.bff.devices.login.login.refresh.application.port.out.RefreshPortOut
import com.menta.bff.devices.login.shared.domain.Refresh
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
class RefreshClient(
    private val webClient: WebClient,
    @Value("\${externals.login.refresh.url}")
    private val url: String,
    @Value("\${externals.login.auth.request-timeout}")
    private val timeout: Long,
    private val toRefreshClientRequestMapper: ToRefreshClientRequestMapper
) : RefreshPortOut {

    override fun refresh(refresh: Refresh): Either<ApplicationError, UserAuth> =
        log.benchmark("refresh") {
            try {
                webClient.post()
                    .uri(URI(url))
                    .bodyValue(refresh.toRequest())
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
            { error("refresh token user failed: {}", it) },
            { info("refresh token user in: {}", it) }
        )

    private fun Refresh.toRequest() =
        toRefreshClientRequestMapper.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun Mono<UserAuth>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while trying to refresh token into the Login Api"))
        }

    companion object : CompanionLogger()
}
