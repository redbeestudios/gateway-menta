package com.kiwi.api.payments.hexagonal.adapter.out.rest

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.PaymentController.Companion.log
import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToTerminalMapper
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.TerminalResponse
import com.kiwi.api.payments.hexagonal.application.port.out.TerminalRepositoryPortOut
import com.kiwi.api.payments.hexagonal.domain.Terminal
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Component
class TerminalRestClient(
    private val toTerminalMapper: ToTerminalMapper,
    @Value("\${externals.entities.terminals.url}")
    private val url: String,
    @Value("\${externals.entities.terminals.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient
) : TerminalRepositoryPortOut {

    override fun retrieve(terminalId: UUID): Terminal =
        (
            doGet(terminalId)
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error with Terminal api")
            )
            .log { info("terminal found: {}", it) }

    private fun doGet(terminalId: UUID): Mono<TerminalResponse> =
        webClient
            .get()
            .uri(url, terminalId)
            .retrieve()
            .bodyToMono(TerminalResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while obtaining the terminal in Terminals Api") }

    private fun TerminalResponse.toDomain(): Terminal =
        toTerminalMapper.map(this)
}
