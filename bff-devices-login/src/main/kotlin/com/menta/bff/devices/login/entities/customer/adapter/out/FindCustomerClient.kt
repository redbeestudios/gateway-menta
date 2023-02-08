package com.menta.bff.devices.login.entities.customer.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.customer.application.port.out.FindCustomerPortOut
import com.menta.bff.devices.login.entities.customer.domain.Customer
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
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
class FindCustomerClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.customers.url}")
    private val url: String,
    @Value("\${externals.entities.customers.request-timeout}")
    private val timeout: Long
) : FindCustomerPortOut {

    override fun findBy(id: UUID): Either<ApplicationError, Customer> =
        log.benchmark("find customer by id $id") {
            try {
                webClient.get()
                    .uri(URI(id.buildUri()))
                    .retrieve()
                    .bodyToMono(Customer::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .leftIfReceiverNull(notFound("customer $id not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching customer $id : {}", it) },
                { info("customer found : {}", it) }
            )
        }

    private fun UUID.buildUri() = "$url/$this"

    private fun Mono<Customer>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while obtaining the customer in Customer Api"))
        }

    companion object : CompanionLogger()
}
