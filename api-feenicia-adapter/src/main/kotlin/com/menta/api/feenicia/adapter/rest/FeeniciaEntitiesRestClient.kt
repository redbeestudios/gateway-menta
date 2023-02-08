package com.menta.api.feenicia.adapter.rest

import com.menta.api.feenicia.application.port.out.FeeniciaMerchantClientRepository
import com.menta.api.feenicia.domain.FeeniciaMerchant
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class FeeniciaEntitiesRestClient(
    @Value("\${externals.entities.acquirer.feenicia-merchants.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.feenicia-merchants.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : FeeniciaMerchantClientRepository {

    override fun findBy(merchantId: UUID): FeeniciaMerchant =
        webClient
            .get()
            .uri(url, merchantId)
            .retrieve()
            .bodyToMono(FeeniciaMerchant::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Merchant Feenicia") }
            .map { it }
            .block()
            ?: throw InternalError("Error with FeeniciaMerchant")
}
