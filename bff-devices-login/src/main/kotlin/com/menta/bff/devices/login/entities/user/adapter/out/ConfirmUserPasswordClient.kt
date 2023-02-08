package com.menta.bff.devices.login.entities.user.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.user.adapter.out.mapper.ToUserRequestMapper
import com.menta.bff.devices.login.entities.user.adapter.out.models.ConfirmPasswordUserRequest
import com.menta.bff.devices.login.entities.user.application.port.out.ConfirmUserPasswordPortOut
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
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
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class ConfirmUserPasswordClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.users.url}")
    private val url: String,
    @Value("\${externals.entities.users.request-timeout}")
    private val timeout: Long,
    private val toUserRequestMapper: ToUserRequestMapper
) : ConfirmUserPasswordPortOut {

    override fun confirm(confirmRestoreUserPassword: ConfirmRestoreUserPassword): Either<ApplicationError, Unit> =
        with(confirmRestoreUserPassword) {
            log.benchmark("confirm user password by email $user and type $userType") {
                try {
                    this
                        .toModel()
                        .doConfirm(user, userType)
                        .block()
                        .log { info("user by email $user and type $userType restore password successfully") }
                        ?.right() ?: Unit.right()
                } catch (e: WebClientResponseException) {
                    clientError(e)
                        .log { error("user by email $user and type $userType not restore password") }
                        .left()
                }
            }
        }

    private fun ConfirmPasswordUserRequest.doConfirm(user: String, type: UserType) =
        webClient.patch()
            .uri(URI(buildUri(user, type)))
            .body(createMono())
            .retrieve()
            .bodyToMono(Unit::class.java)
            .timeout(Duration.ofMillis(timeout))
            .throwIfTimeOutError()

    private fun ConfirmRestoreUserPassword.toModel() =
        toUserRequestMapper.mapFrom(this)

    private fun ConfirmPasswordUserRequest.createMono() = Mono.just(this).log {
        info("mono: {}", it.block())
    }

    private fun buildUri(user: String, type: UserType) = "$url/$user/type/$type/confirm-forgot-password"

    private fun Mono<Unit>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the user in User Api"))
        }

    companion object : CompanionLogger()
}
