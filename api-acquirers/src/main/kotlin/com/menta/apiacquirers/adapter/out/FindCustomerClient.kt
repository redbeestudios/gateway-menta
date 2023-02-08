package com.menta.apiacquirers.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.apiacquirers.adapter.out.mapper.ToCustomerMapper
import com.menta.apiacquirers.adapter.out.model.CustomerResponse
import com.menta.apiacquirers.application.port.out.FindCustomerPortOut
import com.menta.apiacquirers.domain.Customer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.clientError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.customerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.timeoutError
import com.menta.apiacquirers.shared.util.leftIfReceiverNull
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.UUID

@Component
class FindCustomerClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.domain.customers.url}")
    private val url: String,
    @Value("\${externals.entities..domain.customers.request-timeout}")
    private val timeout: Long,
    private val toCustomerMapper: ToCustomerMapper
) : FindCustomerPortOut {

    override fun findBy(id: UUID): Either<ApplicationError, Customer> =
        log.benchmark("find customer by id $id") {
            try {
                webClient.get()
                    .uri(URI(id.buildUri()))
                    .retrieve()
                    .bodyToMono(CustomerResponse::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(customerNotFound(id))
                    .logRight { info("customer found: {}", it) }
            } catch (e: WebClientResponseException) {
                when (e.statusCode) {
                    NOT_FOUND -> {
                        e.log { error("customer not found: {}", id) }
                            .let { customerNotFound(id).left() }
                    }
                    REQUEST_TIMEOUT -> {
                        e.log { error("customer searching: {}", it) }
                            .let { timeoutError().left() }
                    }
                    else -> {
                        e.log { error("error searching customer: {} {} {}", it.statusCode, it.responseBodyAsString, it.cause) }
                            .let { clientError(e).left() }
                    }
                }
            }
        }

    private fun Mono<CustomerResponse>.toDomain() =
        this.map { toCustomerMapper.mapFrom(it) }

    private fun UUID.buildUri() = "$url/$this"

    companion object : CompanionLogger()
}
