package com.menta.bff.devices.login.entities.tags.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.tags.application.port.out.FindAcquirerTagPortOut
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.shared.domain.UserAuth
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
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class FindAcquirerTagClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.acquirers-tags.url}")
    private val url: String,
    @Value("\${externals.entities.acquirers-tags.request-timeout}")
    private val timeout: Long
) : FindAcquirerTagPortOut {

    override fun findBy(
        customerId: UUID,
        type: String,
        userAuth: UserAuth
    ): Either<ApplicationError, List<AcquirerTag>> =
        log.benchmark("find acquirers tags by customer id $customerId and type $type") {
            try {
                webClient.get()
                    .uri(URI(customerId.buildUri(type)))
                    .header("Authorization", "Bearer ${userAuth.token?.idToken}")
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("acquirers tags for customer id $customerId and type $type not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching acquirer tags for customer id $customerId and type $type : {}", it) },
                { info("acquirer tags for customer id $customerId and type $type found : {}", it) }
            )
        }

    private fun UUID.buildUri(type: String) = "$url?customerId=$this&type=$type"

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the acquirers tags in Acquirers Tags Api"))
        }

    private fun Mono<Page>.toDomain() = this.map { it.tags }

    companion object : CompanionLogger()
}

data class Page(
    val tags: List<AcquirerTag>
)
