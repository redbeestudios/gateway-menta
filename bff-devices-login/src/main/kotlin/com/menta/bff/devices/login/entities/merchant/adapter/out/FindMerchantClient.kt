package com.menta.bff.devices.login.entities.merchant.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.merchant.application.out.FindMerchantPortOut
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
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
class FindMerchantClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.merchants.url}")
    private val url: String,
    @Value("\${externals.entities.merchants.request-timeout}")
    private val timeout: Long
) : FindMerchantPortOut {

    override fun findBy(id: UUID): Either<ApplicationError, Merchant> =
        log.benchmark("find merchant by id $id") {
            try {
                webClient.get()
                    .uri(URI(id.buildUri()))
                    .retrieve()
                    .bodyToMono(Merchant::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .block()
                    .leftIfReceiverNull(notFound("merchant $id not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching merchant id $id : {}", it) },
                { info("merchant id $id found : {}", it) }
            )
        }

    private fun UUID.buildUri() = "$url/$this"

    private fun Mono<Merchant>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while obtaining the merchant in Merchant Api"))
        }

    companion object : CompanionLogger()
}
