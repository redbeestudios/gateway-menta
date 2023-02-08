package com.kiwi.api.payments.adapter.rest

import com.kiwi.api.payments.application.port.out.AcquirerCustomerRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerCustomer
import com.kiwi.api.payments.shared.util.GlobalContants.Companion.GPS
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class AcquirerCustomerRestClient(
    @Value("\${externals.entities.acquirer.customer.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.customer.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : AcquirerCustomerRepositoryOutPort {

    override fun findBy(customerId: UUID): AcquirerCustomer? =
        try {
            webClient
                .get()
                .uri(url, customerId, GPS)
                .retrieve()
                .bodyToMono(AcquirerCustomer::class.java)
                .timeout(Duration.ofMillis(timeout))
                .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Customer Acquirer") }
                .map { it }
                .block()
                ?: throw InternalError("Error with AcquirerCustomer")
        } catch (e: Exception) {
            null
        }
}
