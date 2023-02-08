package com.menta.bff.devices.login.login.revoke.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.login.revoke.adapter.out.model.mapper.ToRevokeTokenClientRequestMapper
import com.menta.bff.devices.login.login.revoke.application.port.out.RevokeTokenPortOut
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.exception.ApplicationErrorException
import com.menta.bff.devices.login.shared.other.util.leftIfReceiverNull
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import com.menta.bff.devices.login.shared.other.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class RevokeTokenClient(
    private val webClient: WebClient,
    @Value("\${externals.login.revoke.token.url}")
    private val url: String,
    @Value("\${externals.login.revoke.token.request-timeout}")
    private val timeout: Long,
    private val toRevokeTokenClientRequestMapper: ToRevokeTokenClientRequestMapper
) : RevokeTokenPortOut {

    override fun revoke(revokeToken: RevokeToken): Either<ApplicationError, Unit> =
        log.benchmark("revoke token") {
            try {
                webClient.post()
                    .uri(URI(url))
                    .bodyValue(revokeToken.toRequest())
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .leftIfReceiverNull(ApplicationError.revokeTokenError())
                    .void()
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }
        }.logEither(
            { error("revoke token user failed: {}", it) },
            { info("user revoke token successful") }
        )

    private fun RevokeToken.toRequest() =
        toRevokeTokenClientRequestMapper.mapFrom(this)
            .log { info("request mapped: {}", it) }

    private fun Mono<ResponseEntity<Void>>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while trying to revoke token into the Login Api"))
        }

    companion object : CompanionLogger()
}
