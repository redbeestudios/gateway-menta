package com.menta.apiacquirers.adapter.out

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.menta.apiacquirers.adapter.out.provider.AcquirerAwareUriProvider
import com.menta.apiacquirers.application.port.out.AcquirerRepositoryOutPort
import com.menta.apiacquirers.domain.OperableAcquirers.Acquirer
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.serverError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.unknownRepositoryError
import com.menta.apiacquirers.shared.util.leftIfReceiverNull
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.log.benchmark
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AcquirerRepository(
    private val webClient: WebClient,
    private val uriProvider: AcquirerAwareUriProvider
) : AcquirerRepositoryOutPort {

    override fun execute(
        operation: JsonNode,
        acquirer: Acquirer,
        operationType: OperationType
    ): Either<ApplicationError, ResponseEntity<JsonNode>> =
        log.benchmark("do operation") {
            buildUri(acquirer, operationType).flatMap { uri ->
                try {
                    webClient
                        .post()
                        .uri(uri)
                        .body(Mono.just(operation), JsonNode::class.java)
                        .retrieve()
                        .onStatus(HttpStatus::isError) { handleError(it) }
                        .toEntity(JsonNode::class.java)
                        .block()
                        .leftIfReceiverNull(unknownRepositoryError())
                        .map { ResponseEntity(it.body, it.statusCode) }
                } catch (e: RepositoryException) {
                    e.responseEntity.right()
                } catch (e: Exception) {
                    serverError(e).left()
                }
            }.logEither(
                { error("error communicating with acquirer: {}", it) },
                { info("acquirer response: {}", it.statusCode) }
            )
        }

    private fun buildUri(acquirer: Acquirer, operationType: OperationType) =
        uriProvider.provideFor(acquirer, operationType)
            .logRight { info("uri build: {}", it) }

    private fun handleError(response: ClientResponse): Mono<out Throwable> =
        response.toEntity(JsonNode::class.java).map { RepositoryException(it) }

    companion object : CompanionLogger()
}

class RepositoryException(
    val responseEntity: ResponseEntity<JsonNode>
) : RuntimeException()
