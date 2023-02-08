package com.menta.bff.devices.login.entities.merchant.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.merchant.application.out.FindTaxMerchantPortOut
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
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
class FindTaxMerchantClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.merchants-taxes-entities.url}")
    private val url: String,
    @Value("\${externals.entities.merchants.request-timeout}")
    private val timeout: Long
) : FindTaxMerchantPortOut {

    override fun findBy(merchantId: UUID): Either<ApplicationError, TaxMerchant> =
        log.benchmark("find tax merchant by id $merchantId") {
            try {
                webClient.get()
                    .uri(URI(merchantId.buildUri()))
                    .retrieve()
                    .bodyToMono(TaxMerchant::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .leftIfReceiverNull(ApplicationError.notFound("tax merchant $merchantId not found"))
            } catch (e: WebClientResponseException) {
                ApplicationError.clientError(e).left()
            }.logEither(
                { error("error searching tax merchant whit id $merchantId : {}", it) },
                { info("tax merchant whit id $merchantId found : {}", it) }
            )
        }

    private fun UUID.buildUri() = "$url/$this"

    private fun Mono<TaxMerchant>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while obtaining the tax merchant in Api Taxes Entities"))
        }

    companion object : CompanionLogger()
}
