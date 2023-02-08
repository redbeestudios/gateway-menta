package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.ReimbursementController.Companion.log
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper.ToAcquirerRequestMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper.ToAuthorizationMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.AcquirerResponse
import com.kiwi.api.reimbursements.hexagonal.application.port.out.AcquirerRepository
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
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
    private val urlAnnulment: String,
    @Value("\${externals.entities.acquirer.refunds.url}")
    private val urlRefund: String,
    @Value("\${externals.entities.acquirer.request-timeout}")
    private val requestTimeout: Long,
    private val webClient: WebClient
) : AcquirerRepository {

    override fun authorizeAnnulment(annulment: Annulment): Authorization {
        return (
            annulment
                .toModel()
                .authorize(urlAnnulment)
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Acquirer")
            )
            .log { info("Annulment authorized: {}", it) }
    }

    override fun authorizeRefund(refund: Refund): Authorization {
        return (
            refund
                .toModel()
                .authorize(urlRefund)
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Acquirer")
            )
            .log { info("Refund authorized: {}", it) }
    }

    private fun AcquirerRequest.authorize(url: String): Mono<AcquirerResponse> =
        webClient
            .post()
            .uri(url)
            .header("Country", customer.country)
            .body(createMono())
            .retrieve()
            .bodyToMono(AcquirerResponse::class.java)
            .timeout(Duration.ofMillis(requestTimeout))
            .onErrorMap(TimeoutException::class.java) {
                HttpTimeoutException("Connection timeout while authorizing at the Acquirer")
            }

    private fun AcquirerRequest.createMono() = Mono.just(this).log {
        info("mono: {}", it.block())
    }

    private fun Refund.toModel(): AcquirerRequest =
        toAcquirerRequestMapper.map(this)
            .log {
                info("created request for adpater: {}", it)
            }

    private fun Annulment.toModel(): AcquirerRequest =
        toAcquirerRequestMapper.map(this).log {
            info("created request for adpater: {}", it)
        }

    private fun AcquirerResponse.toDomain(): Authorization =
        toAuthorizationMapper.map(this)
}
