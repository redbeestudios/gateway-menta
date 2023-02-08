package com.menta.bff.devices.login.entities.terminals.adapter.out

import arrow.core.Either
import arrow.core.left
import com.fasterxml.jackson.annotation.JsonProperty
import com.menta.bff.devices.login.entities.terminals.application.port.out.FindTerminalPortOut
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
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
import java.util.concurrent.TimeoutException

@Component
class FindTerminalClient(
    private val webClient: WebClient,
    @Value("\${externals.entities.terminals.url}")
    private val url: String,
    @Value("\${externals.entities.terminals.request-timeout}")
    private val timeout: Long
) : FindTerminalPortOut {

    override fun findBy(serialCode: String): Either<ApplicationError, Terminal> =
        log.benchmark("find terminal by serialCode $serialCode") {
            try {
                webClient.get()
                    .uri(URI(buildUri(serialCode)))
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .throwIfTimeOutError()
                    .toDomain()
                    .block()
                    .leftIfReceiverNull(notFound("terminal $serialCode not found"))
            } catch (e: WebClientResponseException) {
                clientError(e).left()
            }.logEither(
                { error("error searching terminal with serialCode $serialCode : {}", it) },
                { info("terminal with serialCode $serialCode found : {}", it) }
            )
        }

    private fun buildUri(serialCode: String) = "$url?serialCode=$serialCode"

    private fun Mono<Page>.toDomain() = this.map { it.embedded.terminals.firstOrNull() }

    private fun Mono<Page>.throwIfTimeOutError() =
        this.onErrorMap(TimeoutException::class.java) {
            ApplicationErrorException(ApplicationError.timeoutError("Connection timeout while obtaining the terminal in Terminal Api"))
        }

    companion object : CompanionLogger()
}

data class Page(
    @JsonProperty("_embedded")
    val embedded: Embedded
) {
    data class Embedded(
        val terminals: List<Terminal>
    )
}
