package com.menta.apiacquirers.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.apiacquirers.adapter.out.mapper.ToAcquirerCustomerMapper
import com.menta.apiacquirers.adapter.out.model.AcquirerCustomerResponse
import com.menta.apiacquirers.application.port.out.FindAcquirerCustomerPortOut
import com.menta.apiacquirers.domain.AcquirerCustomer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.clientError
import com.menta.apiacquirers.shared.util.leftIfReceiverNull
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.UUID

@Component
class FindAcquirerCustomerClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.domain.customers.url}")
    private val url: String,
    @Value("\${externals.entities.domain.customers.request-timeout}")
    private val timeout: Long,
    private val toAcquirerCustomer: ToAcquirerCustomerMapper
) : FindAcquirerCustomerPortOut {

    override fun findBy(id: UUID, acquirer: String): Either<ApplicationError, AcquirerCustomer> =
        log.benchmark("find acquirer customer by id $id and acquirer $acquirer") {
            try {
                webClient.get()
                    .uri(URI(buildUri(id, acquirer)))
                    .retrieve()
                    .bodyToMono(AcquirerCustomerResponse::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(acquirerCustomerNotFound(id, acquirer))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }
                .logEither(
                    { error("acquirer customer {} not found: {}", id, it) },
                    { info("acquirer customer found: {}", it) }
                )
        }

    private fun Mono<AcquirerCustomerResponse>.toDomain() =
        this.map { toAcquirerCustomer.mapFrom(it) }

    private fun buildUri(id: UUID, acquirer: String) =
        "$url/$id/acquirers/$acquirer"

    companion object : CompanionLogger()
}
