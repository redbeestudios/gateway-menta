package com.menta.apisecrets.adapter.out

import arrow.core.Either
import arrow.core.left
import com.fasterxml.jackson.annotation.JsonProperty
import com.menta.apisecrets.adapter.out.model.TerminalResponse
import com.menta.apisecrets.adapter.out.model.mapper.ToTerminalMapper
import com.menta.apisecrets.application.port.out.TerminalRepositoryOutPort
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.terminalNotFound
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.terminalRepositoryError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.timeout
import com.menta.apisecrets.shared.util.leftIfNullOrEmpty
import com.menta.apisecrets.shared.util.log.CompanionLogger
import com.menta.apisecrets.shared.util.log.benchmark
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class TerminalRepository(
    private val toTerminalMapper: ToTerminalMapper,
    private val webClient: WebClient,
    private val uriProvider: UriProvider,
    @Value("\${externals.entities.terminals.timeout}")
    private val timeout: Long
) : TerminalRepositoryOutPort {

    override fun execute(terminalSerialCode: String): Either<ApplicationError, List<Terminal>> =
        log.benchmark("find terminal by serial code") {
            try {
                webClient.get()
                    .uri(buildUriFor(terminalSerialCode))
                    .retrieve()
                    .bodyToMono(Page::class.java)
                    .timeout(Duration.ofMillis(timeout))
                    .map { it.embedded.terminals.map { it.toDomain() } }
                    .block()
                    .leftIfNullOrEmpty { terminalNotFound(terminalSerialCode) }
                    .logEither(
                        { error("error while searching for terminal: {}", it) },
                        { info("terminal found: {}", it) }
                    )
            } catch (e: WebClientResponseException) {
                when (e.statusCode) {
                    NOT_FOUND -> {
                        e.log { error("terminal not found: {}", terminalSerialCode) }
                            .let { terminalNotFound(terminalSerialCode).left() }
                    }
                    REQUEST_TIMEOUT -> {
                        e.log { error("timeout searching for terminal: {}", it) }
                            .let { timeout().left() }
                    }
                    else -> {
                        e.log {
                            error(
                                "error searching terminal: {} {} {}",
                                it.statusCode,
                                it.responseBodyAsString,
                                it.cause
                            )
                        }
                            .let { terminalRepositoryError().left() }
                    }
                }
            }
        }

    private fun buildUriFor(serialCode: String) =
        uriProvider.provideForTerminals(mapOf(SERIAL_CODE_QUERY_PARAM to serialCode))

    private fun TerminalResponse.toDomain() =
        toTerminalMapper.mapFrom(this)
            .log { info("domain obtained: {}", it) }

    companion object : CompanionLogger() {
        private const val SERIAL_CODE_QUERY_PARAM = "serialCode"
    }
}


data class Page(
    @JsonProperty("_embedded")
    val embedded: Embedded
) {
    data class Embedded(
        val terminals: List<TerminalResponse>
    )
}