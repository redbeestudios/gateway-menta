package com.kiwi.api.payments.hexagonal.adapter.out.rest

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.PaymentController.Companion.log
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToCustomerMapper
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.CustomerResponse
import com.kiwi.api.payments.hexagonal.application.port.out.CustomerRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.Payment
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class CustomerRestClient(
    private val toCustomerMapper: ToCustomerMapper,
    @Value("\${externals.entities.customers.url}")
    private val url: String,
    @Value("\${externals.entities.customers.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : CustomerRepositoryPortOut {

    override fun retrieve(customerId: UUID): Payment.Customer =
        (
            doGet(customerId)
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Customer api")
            )
            .log { info("customer found: {}", it) }

    private fun doGet(customerId: UUID): Mono<CustomerResponse> =
        webClient
            .get()
            .uri(url, customerId)
            .retrieve()
            .bodyToMono(CustomerResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while obtaining the customer in Customers Api") }

    private fun CustomerResponse.toDomain(): Payment.Customer =
        toCustomerMapper.map(this)
}
