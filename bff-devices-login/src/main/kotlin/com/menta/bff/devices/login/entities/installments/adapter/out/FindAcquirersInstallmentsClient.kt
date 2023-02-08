package com.menta.bff.devices.login.entities.installments.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.installments.application.port.out.FindAcquirersInstallmentsPortOut
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
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
class FindAcquirersInstallmentsClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.installments.url}")
    private val url: String,
    @Value("\${externals.entities.installments.request-timeout}")
    private val timeout: Long
) : FindAcquirersInstallmentsPortOut {

    override fun findBy(merchantId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerInstallment>> =
        log.benchmark("find acquirers installments by merchantId $merchantId") {
            try {
                webClient.get()
                    .uri(URI(merchantId.buildUri()))
                    .header("Authorization", "Bearer ${userAuth.token?.idToken}")
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("acquirers installments for merchant id : $merchantId not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching acquirer installments for merchant id $merchantId : {}", it) },
                { info("acquirer installments for merchant id $merchantId found : {}", it) }
            )
        }

    private fun UUID.buildUri() = "$url?merchantId=$this"

    private fun Mono<Page>.toDomain() = this.map { it.installments }

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(timeoutError("Connection timeout while obtaining the installments in Installment Api"))
        }

    companion object : CompanionLogger()
}

data class Page(
    val installments: List<AcquirerInstallment>
)
