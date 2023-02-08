package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.ReimbursementController.Companion.log
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper.ToTerminalMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.model.TerminalResponse
import com.kiwi.api.reimbursements.hexagonal.application.port.out.TerminalRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
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
            terminalId
                .doAuthorize()
                .map { it.toDomain() }.block()
                ?: throw InternalError("Error getting Terminal")
            )
            .log { info("terminal found: {}", it) }

    private fun UUID.doAuthorize(): Mono<TerminalResponse> =
        webClient
            .get()
            .uri(url, this.toString())
            .retrieve()
            .bodyToMono(TerminalResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while obtaining the terminal in Terminals Api") }

    private fun TerminalResponse.toDomain(): Terminal =
        toTerminalMapper.map(this)
}
