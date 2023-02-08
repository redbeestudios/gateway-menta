package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.ReimbursementController.Companion.log
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper.ToMerchantMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.MerchantResponse
import com.kiwi.api.reimbursements.hexagonal.application.port.out.MerchantRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
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
            merchantId
                .doAuthorize()
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error getting Merchant")
            )
            .log { info("merchant found: {}", it) }

    private fun UUID.doAuthorize(): Mono<MerchantResponse> =
        webClient
            .get()
            .uri(url, this.toString())
            .retrieve()
            .bodyToMono(MerchantResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while obtaining the merchant in Merchants Api") }

    private fun MerchantResponse.toDomain(): Merchant =
        toMerchantMapper.map(this)
}
