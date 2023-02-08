package com.menta.api.credibanco.adapter.rest

import com.menta.api.credibanco.application.port.out.CredibancoMerchantRepositoryPortOut
import com.menta.api.credibanco.domain.CredibancoMerchant
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class CredibancoMerchantRestClient(
    @Value("\${externals.entities.credibanco.merchant.url}")
    private val url: String,
    @Value("\${externals.entities.credibanco.merchant.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : CredibancoMerchantRepositoryPortOut {

    override fun findBy(merchantId: UUID): CredibancoMerchant =
        webClient
            .get()
            .uri(url, merchantId)
            .retrieve()
            .bodyToMono(CredibancoMerchant::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Credibanco Merchant") }
            .map { it }
            .block()
            ?: throw InternalError("Error with Credibanco Merchant")
}
