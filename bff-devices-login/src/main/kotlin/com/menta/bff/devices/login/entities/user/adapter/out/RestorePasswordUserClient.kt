package com.menta.bff.devices.login.entities.user.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.user.application.port.out.RestoreUserPasswordPortOut
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
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
class RestorePasswordUserClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.users.url}")
    private val url: String,
    @Value("\${externals.entities.users.request-timeout}")
    private val timeout: Long
) : RestoreUserPasswordPortOut {

    override fun resolve(user: String, type: UserType): Either<ApplicationError, Unit> =
        log.benchmark("restore user password by email $user and type $type") {
            try {
                webClient.post()
                    .uri(URI(buildUri(user, type)))
                    .retrieve()
                    .bodyToMono(Unit::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .log { info("user by email $user and type $type restore password successfully") }
                    ?.right() ?: Unit.right()
            } catch (e: WebClientResponseException) {
                clientError(e)
                    .log { error("user by email $user and type $type not restore password") }
                    .left()
            }
        }

    private fun buildUri(email: Email, type: UserType) = "$url/$email/type/$type/forgot-password"

    private fun Mono<Unit>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the user in User Api"))
        }

    companion object : CompanionLogger()
}
