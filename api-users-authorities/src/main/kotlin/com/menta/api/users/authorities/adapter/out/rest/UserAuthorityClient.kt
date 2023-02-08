package com.menta.api.users.authorities.adapter.out.rest

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.users.authorities.adapter.out.rest.mapper.ToAssignAuthorityUserRequestMapper
import com.menta.api.users.authorities.adapter.out.rest.model.AssignAuthorityUserRequest
import com.menta.api.users.authorities.application.port.out.AssignAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.timeoutError
import com.menta.api.users.authorities.shared.other.error.model.exception.ApplicationErrorException
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import com.menta.api.users.authorities.shared.other.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class UserAuthorityClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.users.url}")
    private val url: String,
    @Value("\${externals.entities.users.request-timeout}")
    private val timeout: Long,
    private val toAssignAuthorityUserRequestMapper: ToAssignAuthorityUserRequestMapper
) : AssignAuthorityPortOut {

    override fun assign(userAssignAuthority: UserAssignAuthority): Either<ApplicationError, Unit> =
        with(userAssignAuthority) {
            log.benchmark("assign user authority $authority by email $user and type $type") {
                try {
                    authority
                        .toModel()
                        .doAssign(user, type)
                        .block()
                        .log { info("assign user authority $authority by email $user and type $type successfully") }
                        ?.right() ?: Unit.right()
                } catch (e: WebClientResponseException) {
                    clientError(e)
                        .log { error("error assign user authority $authority by email $user and type $type") }
                        .left()
                }
            }
        }

    private fun String.toModel() = toAssignAuthorityUserRequestMapper.mapFrom(this)

    private fun AssignAuthorityUserRequest.doAssign(user: String, type: UserType) =
        webClient.post()
            .uri(URI(buildUri(user, type)))
            .bodyValue(this)
            .retrieve()
            .bodyToMono(Unit::class.java)
            .timeout(Duration.ofMillis(timeout))
            .throwIfTimeOutError()

    private fun buildUri(user: String, type: UserType) = "$url/$user/type/$type/groups"

    private fun Mono<Unit>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while assign authority in User Api"))
        }

    companion object : CompanionLogger()
}
