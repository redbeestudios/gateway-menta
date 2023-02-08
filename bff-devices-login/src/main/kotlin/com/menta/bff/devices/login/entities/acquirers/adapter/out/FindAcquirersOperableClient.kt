package com.menta.bff.devices.login.entities.acquirers.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.acquirers.application.port.out.FindAcquirersOperablePortOut
import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
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
class FindAcquirersOperableClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.acquirers.url}")
    private val url: String,
    @Value("\${externals.entities.acquirers.request-timeout}")
    private val timeout: Long
) : FindAcquirersOperablePortOut {

    override fun findBy(customerId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerOperable>> =
        log.benchmark("find acquirers operable by customer id $customerId") {
            try {
                webClient.get()
                    .uri(URI(customerId.buildUri()))
                    .header("Authorization", "Bearer ${userAuth.token?.idToken}")
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("acquirers operable for customer id $customerId not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching acquirer operable for customer id $customerId : {}", it) },
                { info("acquirer operable for customer id $customerId found : {}", it) }
            )
        }

    private fun UUID.buildUri() = "$url/operable?customerId=$this"

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the acquirers in Acquirer Api"))
        }

    private fun Mono<Page>.toDomain() = this.map { it.acquirers }

    companion object : CompanionLogger()
}

data class Page(
    val acquirers: List<AcquirerOperable>
)
