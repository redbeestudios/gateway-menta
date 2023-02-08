package com.menta.api.feenicia.adapter.rest

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToCreatedOperationMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaRefundRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToFeeniciaReverseRequestMapper
import com.menta.api.feenicia.adapter.rest.mapper.ToRequestHeaderMapper
import com.menta.api.feenicia.adapter.rest.model.FeeniciaResponse
import com.menta.api.feenicia.adapter.rest.provider.AesEncryptionProvider
import com.menta.api.feenicia.adapter.rest.provider.FeeniciaUrlProvider
import com.menta.api.feenicia.application.port.out.FeeniciaClientRepository
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.domain.OperationType.REVERSAL
import com.menta.api.feenicia.shared.error.leftIfNull
import com.menta.api.feenicia.shared.error.model.ApplicationError
import com.menta.api.feenicia.shared.error.model.FeeniciaResponseError
import com.menta.api.feenicia.shared.error.model.ServerError
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import reactor.core.publisher.Mono
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class FeeniciaRestClient(
    private val webClient: WebClient,
    @Value("\${api-feenicia-adapter.timeout}")
    private val timeout: Long,
    private val toCreatedOperationMapper: ToCreatedOperationMapper,
    private val toFeeniciaRequestMapper: ToFeeniciaRequestMapper,
    private val toFeeniciaRefundRequestMapper: ToFeeniciaRefundRequestMapper,
    private val toFeeniciaReverseRequestMapper: ToFeeniciaReverseRequestMapper,
    private val toRequestHeaderMapper: ToRequestHeaderMapper,
    private val feeniciaUrlProvider: FeeniciaUrlProvider,
    @Value("\${api-feenicia-adapter.properties.responseIv}")
    private val responseIv: String,
    @Value("\${api-feenicia-adapter.properties.responseKey}")
    private val responseKey: String,
    private val aesEncryptionProvider: AesEncryptionProvider,
    private val mapper: ObjectMapper
) : FeeniciaClientRepository {

    override fun execute(operation: Operation, reverseOperationType: OperationType?): Either<ApplicationError, CreatedOperation> =
        (
            if (operation.operationType == REVERSAL && reverseOperationType == REFUND) {
                operation
                    .toModel(operation.operationType)
                    .createEmptyMono()
                    .buildResponse()
            } else {
                operation
                    .toModel(operation.operationType)
                    .doPost(
                        feeniciaUrlProvider.provide(
                            operation,
                            operation.operationType
                        ),
                        operation.feeniciaMerchant.merchant,
                        operation.feeniciaMerchant.keys.requestSignatureKey.decrypt(responseIv, responseKey),
                        operation.feeniciaMerchant.keys.requestSignatureIv.decrypt(responseIv, responseKey)
                    ).buildResponse()
            }
            )

    private fun buildHeaders(merchantId: String, signatureKey: String, signatureIV: String, request: Any) =
        toRequestHeaderMapper.provide(merchantId, signatureKey, signatureIV, request.toSHA256()).log { info("header: {}", it) }

    private fun Mono<FeeniciaResponse>.buildResponse(): Either<ApplicationError, CreatedOperation> =
        this.map { it.toDomain() }
            .block()
            .leftIfNull(ServerError())
            .mapLeft { FeeniciaResponseError(it.code) }

    private inline fun <reified T : Any> T.doPost(url: String, merchantId: String, signatureKey: String, signatureIV: String): Mono<FeeniciaResponse> =
        webClient
            .post()
            .uri(url)
            .headers { header -> header.setAll(buildHeaders(merchantId, signatureKey, signatureIV, this)) }
            .body(createMono())
            .retrieve()
            .bodyToMono(FeeniciaResponse::class.java)
            .timeout(Duration.ofMillis(timeout))
            .onErrorMap(TimeoutException::class.java) { HttpTimeoutException("Connection timeout while post to Feenicia Acquirer") }

    private inline fun <reified T : Any> T.createEmptyMono(): Mono<FeeniciaResponse> =
        Mono.just(FeeniciaResponse(responseCode = "00"))

    private fun <T> T.toSHA256(): String =
        DigestUtils("SHA-256").digestAsHex(
            mapper.writeValueAsString(this).trimIndent().log { info("json request: {}", it) }
        )

    private fun <T : Any> T.createMono() = Mono.just(this).log {
        info("mono: {}", it.block())
    }

    private fun Operation.toModel(operationType: OperationType): Any =
        when (operationType) {
            PAYMENT -> toFeeniciaRequestMapper.map(this)
            REVERSAL -> toFeeniciaReverseRequestMapper.map(this)
            else -> toFeeniciaRefundRequestMapper.map(this)
        }

    private fun FeeniciaResponse.toDomain(): CreatedOperation =
        toCreatedOperationMapper.map(this)
            .log {
                info("Created Operation {}", it)
            }

    private fun String.decrypt(requestIv: String, requestKey: String) =
        aesEncryptionProvider.decrypt(requestKey, requestIv, this)

    companion object : CompanionLogger()
}
