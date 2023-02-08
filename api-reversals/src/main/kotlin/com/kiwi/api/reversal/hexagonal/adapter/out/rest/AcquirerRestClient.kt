package com.kiwi.api.reversal.hexagonal.adapter.out.rest

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.ReversalController.Companion.log
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper.ToAcquirerRequestMapper
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper.ToAuthorizationMapper
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reversal.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Authorization
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class AcquirerRestClient(
    private val toAuthorizationMapper: ToAuthorizationMapper,
    private val toAcquirerRequestMapper: ToAcquirerRequestMapper,
    @Value("\${externals.entities.acquirer.annulments.url}")
    private val urlAnnulments: String,
    @Value("\${externals.entities.acquirer.refunds.url}")
    private val urlRefunds: String,
    @Value("\${externals.entities.acquirer.payments.url}")
    private val urlPayments: String,
    @Value("\${externals.entities.acquirer.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : AcquirerRepository {

    override fun authorize(annulment: Annulment): Authorization {
        return (
            annulment
                .toModel()
                .authorize(urlAnnulments)
                .map { it.toAuthorization() }.block()
                ?: throw InternalError(ACQUIRER_ERROR)
            )
            .log { info("annulment reversed: {}", it) }
    }

    override fun authorize(payment: Payment): Authorization {
        return (
            payment
                .toModel()
                .authorize(urlPayments)
                .map { it.toAuthorization() }.block()
                ?: throw InternalError(ACQUIRER_ERROR)
            )
            .log { info("payment reversed: {}", it) }
    }

    override fun authorize(refund: Refund): Authorization {
        return (
            refund
                .toModel()
                .authorize(urlRefunds)
                .map { it.toAuthorization() }.block()
                ?: throw InternalError(ACQUIRER_ERROR)
            )
            .log { info("refund reversed: {}", it) }
    }

    private fun AcquirerRequest.authorize(url: String): Mono<AcquirerResponse> =
        webClient
            .post()
            .uri(url)
            .header("Country", customer.country)
            .body(Mono.just(this))
            .retrieve()
            .bodyToMono(AcquirerResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while authorizing at the Acquirer") }

    private fun Refund.toModel(): AcquirerRequest =
        toAcquirerRequestMapper.map(this)
            .log {
                info(CREATED_MESSAGE, it)
            }

    private fun Payment.toModel(): AcquirerRequest =
        toAcquirerRequestMapper.map(this)
            .log {
                info(CREATED_MESSAGE, it)
            }

    private fun Annulment.toModel(): AcquirerRequest =
        toAcquirerRequestMapper.map(this)
            .log {
                info(CREATED_MESSAGE, it)
            }

    private fun AcquirerResponse.toAuthorization() =
        toAuthorizationMapper.map(this)
            .log {
                info("Created authorization {}", it)
            }

    companion object {
        private const val ACQUIRER_ERROR = "Error with Acquirer"
        private const val CREATED_MESSAGE = "created request for adapter: {}"
    }
}
