package com.kiwi.api.reversal.hexagonal.adapter.out.rest

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.ReversalController.Companion.log
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper.ToMerchantMapper
import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reversal.hexagonal.application.port.out.MerchantRepositoryPortOut
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class MerchantRestClient(
    private val toMerchantMapper: ToMerchantMapper,
    @Value("\${externals.entities.merchants.url}")
    private val url: String,
    @Value("\${externals.entities.merchants.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : MerchantRepositoryPortOut {

    override fun retrieve(merchantId: UUID): Merchant =
        (
            doGet(merchantId)
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Merchant api")
            )
            .log { info("merchant found: {}", it) }

    private fun doGet(merchantId: UUID): Mono<MerchantResponse> =
        webClient
            .get()
            .uri(url, merchantId)
            .retrieve()
            .bodyToMono(MerchantResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while getting at the Merchant api") }

    private fun MerchantResponse.toDomain(): Merchant =
        toMerchantMapper.map(this)
}
