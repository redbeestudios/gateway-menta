package com.menta.api.credibanco.adapter.rest

import com.menta.api.credibanco.application.port.out.CredibancoTerminalRepositoryPortOut
import com.menta.api.credibanco.domain.CredibancoTerminal
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class CredibancoTerminalRestClient(
    @Value("\${externals.entities.credibanco.terminal.url}")
    private val url: String,
    @Value("\${externals.entities.credibanco.terminal.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : CredibancoTerminalRepositoryPortOut {

    override fun findBy(terminalId: UUID): CredibancoTerminal =
        webClient
            .get()
            .uri(url, terminalId)
            .retrieve()
            .bodyToMono(CredibancoTerminal::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Credibanco Terminal") }
            .map { it }
            .block()
            ?: throw InternalError("Error with Credibanco Terminal")
}
