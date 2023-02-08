package com.menta.api.banorte.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.banorte.adapter.http.mapper.ToCreatedOperationMapper
import com.menta.api.banorte.adapter.http.mapper.ToRequestHeaderMapper
import com.menta.api.banorte.application.port.out.BanorteClientRepository
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.leftIfNull
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.error.model.ServerError
import com.menta.api.banorte.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono

@Component
class BanorteHttpClient(
    private val webClient: WebClient,
    private val provider: ToRequestHeaderMapper,
    private val toCreatedOperationMapper: ToCreatedOperationMapper,
    @Value("\${api-banorte-adapter.url}")
    private val url: String,
) : BanorteClientRepository {

    override fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation> =
        operation
            .autorize()
            .map {
                it
                    .log { info("Response Header: {}", it) }
                    .toDomain()
            }.block()
            .leftIfNull(ServerError())
            .flatMap { it }

    fun Operation.autorize(): Mono<ResponseEntity<Void>> =
        webClient
            .post()
            .uri(url)
            .headers { header -> header.setAll(buildHeaders(this)) }
            .retrieve()
            .toEntity()

    fun ResponseEntity<Void>.toDomain() =
        toCreatedOperationMapper.map(this.headers)

    fun buildHeaders(operation: Operation) = provider.map(operation)
        .log { info("Request Header: {}", it) }

    companion object : CompanionLogger()
}
