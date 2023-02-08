package com.kiwi.api.payments.adapter.rest

import com.kiwi.api.payments.application.port.out.AcquirerTerminalRepositoryOutPort
import com.kiwi.api.payments.domain.AcquirerTerminal
import com.kiwi.api.payments.shared.util.GlobalContants
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class AcquirerTerminalRestClient(
    @Value("\${externals.entities.acquirer.terminal.url}")
    private val url: String,
    @Value("\${externals.entities.acquirer.terminal.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : AcquirerTerminalRepositoryOutPort {

    override fun findBy(terminalId: UUID): AcquirerTerminal =
        webClient
            .get()
            .uri(url, terminalId, GlobalContants.GPS)
            .retrieve()
            .bodyToMono(AcquirerTerminal::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while find Terminal Acquirer") }
            .map { it }
            .block()
            ?: throw InternalError("Error with Acquirer Terminal")
}
