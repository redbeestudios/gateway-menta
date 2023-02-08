package com.menta.bff.devices.login.entities.user.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.user.application.port.out.FindUserPortOut
import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import com.menta.bff.devices.login.shared.other.error.model.exception.ApplicationErrorException
import com.menta.bff.devices.login.shared.other.util.leftIfReceiverNull
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
class FindUserClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.users.url}")
    private val url: String,
    @Value("\${externals.entities.users.request-timeout}")
    private val timeout: Long
) : FindUserPortOut {

    override fun findBy(email: Email, type: UserType): Either<ApplicationError, User> =
        log.benchmark("find user by email $email and type $type") {
            try {
                webClient.get()
                    .uri(URI(buildUri(email, type)))
                    .retrieve()
                    .bodyToMono(User::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .leftIfReceiverNull(notFound("user $email for $type not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("user by email $email and type $type not found: {}", it) },
                { info("user by email $email and type $type found: {}", it) }
            )
        }

    private fun buildUri(email: Email, type: UserType) = "$url/$email/type/$type"

    private fun Mono<User>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the user in User Api"))
        }

    companion object : CompanionLogger()
}
