package com.kiwi.api.payments.hexagonal.adapter.out.rest

import arrow.core.Either
import arrow.core.left
import arrow.core.rightIfNotNull
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.PaymentController.Companion.log
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToAcquirerRequestMapper
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToAuthorizationMapper
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerRequest
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerResponse
import com.kiwi.api.payments.hexagonal.application.port.out.AcquirerRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.Authorization
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.shared.error.model.AcquirerError
import com.kiwi.api.payments.shared.error.model.AcquirerTimeOutError
import com.kiwi.api.payments.shared.error.model.ApplicationError
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
    @Value("\${externals.entities.acquirer.payments.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.payments.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : AcquirerRepositoryPortOut {

    override fun authorize(payment: Payment): Either<ApplicationError, Authorization> =
        try {
            payment
                .toModel()
                .doAuthorize()
                .map { it.toDomain() }
                .block()
                .rightIfNotNull { AcquirerError() }
                .log { info("payment authorized: {}", it) }
        } catch (ex: Exception) {
            AcquirerTimeOutError().left()
        }

    private fun AcquirerRequest.doAuthorize(): Mono<AcquirerResponse> =
        webClient
            .post()
            .uri(url)
            .header("Country", customer.country)
            .body(createMono())
            .retrieve()
            .bodyToMono(AcquirerResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while authorizing at the Acquirer") }

    private fun AcquirerRequest.createMono() = Mono.just(this).log {
        info("mono: {}", it.block())
    }

    private fun Payment.toModel() =
        toAcquirerRequestMapper.map(this).log {
            info("created request for adapter: {}", it)
        }

    private fun AcquirerResponse.toDomain() =
        toAuthorizationMapper.map(this)
            .log {
                info("Created payment {}", it)
            }
}
