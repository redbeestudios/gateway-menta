package com.kiwi.api.payments.adapter.rest

import com.kiwi.api.payments.application.port.out.AcquirerMerchantRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerMerchant
import com.kiwi.api.payments.shared.util.GlobalContants
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class AcquirerMerchantRestClient(
    @Value("\${externals.entities.acquirer.merchant.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.merchant.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : AcquirerMerchantRepositoryOutPort {

    override fun findBy(merchantId: UUID): AcquirerMerchant =
        webClient
            .get()
            .uri(url, merchantId, GlobalContants.GPS)
            .retrieve()
            .bodyToMono(AcquirerMerchant::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Merchant Acquirer") }
            .map { it }
            .block()
            ?: throw InternalError("Error with Acquirer Merchant")
}
