package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.ReimbursementController.Companion.log
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper.ToCustomerMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.CustomerResponse
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CustomerRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Customer
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

    override fun retrieve(customerId: UUID): Customer =
        (
            customerId
                .doAuthorize()
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Customer api")
            )
            .log { info("customer found: {}", it) }

    private fun UUID.doAuthorize(): Mono<CustomerResponse> =
        webClient
            .get()
            .uri(url, this.toString())
            .retrieve()
            .bodyToMono(CustomerResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while obtaining the customer in Customers Api") }

    private fun CustomerResponse.toDomain(): Customer =
        toCustomerMapper.map(this)
}
