package com.menta.api.banorte.adapter.http

import arrow.core.Either
import arrow.core.left
import com.menta.api.banorte.application.port.out.BanorteMerchantClientRepository
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.shared.error.leftIfNull
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.error.model.ServerError
import com.menta.api.banorte.shared.util.log.CompanionLogger
import com.menta.api.banorte.shared.util.log.benchmark
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.net.URI
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class BanorteEntitiesClient(
    @Value("\${externals.entities.acquirer.banorte-merchant.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.banorte-merchant.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : BanorteMerchantClientRepository {

    override fun findBy(merchantId: String): Either<ApplicationError, BanorteMerchant> =
        log.benchmark("find Banorte merchant by id $merchantId") {
            try {
                webClient
                    .get()
                    .uri(URI(merchantId.buildUri()))
                    .retrieve()
                    .bodyToMono(BanorteMerchant::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Customer Feenicia") }
                    .block()
                    .leftIfNull(ServerError())
            } catch (e: WebClientResponseException) {
                ServerError().left()
            }.logEither(
                { error("error searching Banorte merchant id $merchantId : {}", it) },
                { info("Banorte merchant id $merchantId found : {}", it) }
            )
        }

    private fun String.buildUri() = "$url/$this"

    companion object : CompanionLogger()
}
