package com.menta.apisecrets.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.apisecrets.adapter.out.TerminalRepository.Companion.log
import com.menta.apisecrets.adapter.out.TerminalRepository.Companion.logEither
import com.menta.apisecrets.adapter.out.model.CustomerResponse
import com.menta.apisecrets.adapter.out.model.mapper.ToCustomerMapper
import com.menta.apisecrets.application.port.out.CustomerRepositoryOutPort
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.customerNotFound
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.terminalRepositoryError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.timeout
import com.menta.apisecrets.shared.util.leftIfReceiverOrRightNull
import com.menta.apisecrets.shared.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration
import java.util.UUID

@Component
class CustomerRepository(
    private val webClient: WebClient,
    private val uriProvider: UriProvider,
    private val toCustomerMapper: ToCustomerMapper,
    @Value("\${externals.entities.terminals.timeout}")
    private val timeout: Long
) : CustomerRepositoryOutPort {

    override fun findBy(id: UUID): Either<ApplicationError, Customer> =
        log.benchmark("find customer by id") {
            try {
                webClient.get()
                    .uri(buildUriFor(id))
                    .retrieve()
                    .bodyToMono(CustomerResponse::class.java)
                    .map { toCustomerMapper.from(it) }
                    .timeout(Duration.ofMillis(timeout))
                    .block()
                    .leftIfReceiverOrRightNull(customerNotFound(id))
                    .logEither(
                        { error("error while searching for customer: {}", it) },
                        { info("customer found: {}", it) }
                    )
            } catch (e: WebClientResponseException) {
                when (e.statusCode) {
                    NOT_FOUND -> {
                        e.log { error("customer not found: {}", id) }
                            .let { customerNotFound(id).left() }
                    }
                    REQUEST_TIMEOUT -> {
                        e.log { error("customer searching for terminal: {}", it) }
                            .let { timeout().left() }
                    }
                    else -> {
                        e.log { error("error searching customer: {} {} {}", it.statusCode, it.responseBodyAsString, it.cause) }
                            .let { terminalRepositoryError().left() }
                    }
                }
            }
        }

    private fun buildUriFor(id: UUID) =
        uriProvider.provideForCustomer(id.toString())
}
