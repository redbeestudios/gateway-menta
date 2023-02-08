package com.menta.bff.devices.login.entities.deviceFlow.adapter.out

import arrow.core.Either
import arrow.core.left
import com.menta.bff.devices.login.entities.deviceFlow.application.port.out.FindDeviceFlowPortOut
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
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
import java.util.concurrent.TimeoutException

@Component
class FindDeviceFlowsClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.device-flows.url}")
    private val url: String,
    @Value("\${externals.entities.device-flows.request-timeout}")
    private val timeout: Long
) : FindDeviceFlowPortOut {

    override fun findBy(terminalModel: String, userAuth: UserAuth): Either<ApplicationError, List<DeviceFlow>> =
        log.benchmark("find device flows by terminal model $terminalModel") {
            try {
                webClient.get()
                    .uri(URI(buildUri(terminalModel)))
                    .header("Authorization", "Bearer ${userAuth.token?.idToken}")
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("device flows for terminal model $terminalModel not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching device flows by terminal model $terminalModel : {}", it) },
                { info("device flows by terminal model $terminalModel : {}", it) }
            )
        }

    private fun buildUri(terminalModel: String) = "$url?terminalModel=$terminalModel"

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(
                timeoutError(
                    "Connection timeout while obtaining the device flows in Device Flows Api"
                )
            )
        }

    private fun Mono<Page>.toDomain() = this.map { it.deviceFlows }

    companion object : CompanionLogger()
}

data class Page(
    val deviceFlows: List<DeviceFlow>
)
