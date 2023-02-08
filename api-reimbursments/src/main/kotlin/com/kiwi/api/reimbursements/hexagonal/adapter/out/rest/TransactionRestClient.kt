package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import arrow.core.Either
import arrow.core.right
import com.kiwi.api.reimbursements.hexagonal.application.port.out.TransactionRepositoryPortOut
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TransactionRestClient(/*
    private val toTransactionMapper: ToTransactionMapper,
    @Value("\${externals.entities.transactions.url}")
    private val url: String,
    @Value("\${externals.entities.transactions.request-timeout}")
    private val timeout: Long,
    private val webClient: WebClient,*/
) : TransactionRepositoryPortOut {

    override fun retrieve(paymentId: UUID): Either<ApplicationError, Transaction> =
        Transaction(
            id = UUID.fromString("48394174-2b3a-4875-ba40-6ece6209c1b2")
        ).right()
/*
        log.benchmark("find transaction by acquirer Id") {
            try {
                webClient.get()
                    .uri(buildUriTransactions(paymentId))
                    .retrieve()
                    .bodyToMono(TransactionResponse::class.java)
                    .map { it.toDomain() }
                    .timeout(Duration.ofMillis(timeout))
                    .block()
                    .leftIfReceiverNull(transactionNotFound(paymentId.toString()))
                    .logEither(
                        { error("error while searching for transaction: {}", it) },
                        { info("transaction found: {}", it) }
                    )
            } catch (e: WebClientResponseException) {
                when (e.statusCode) {
                    HttpStatus.NOT_FOUND -> {
                        e.log { error("transaction not found: {}", paymentId) }
                            .let { transactionNotFound(paymentId.toString()).left() }
                    }
                    HttpStatus.REQUEST_TIMEOUT -> {
                        e.log { error("timeout searching for transaction: {}", it) }
                            .let { ApplicationError.timeout().left() }
                    }
                    else -> {
                        e.log {
                            error("error searching transaction: {} {} {}", it.statusCode, it.responseBodyAsString, it.cause)
                        }.let { ApplicationError.transactionRepositoryError().left() }
                    }
                }
            }
        }


    private fun TransactionResponse.toDomain(): Transaction =
        toTransactionMapper.map(this)

    private fun buildUriTransactions(paymentId: UUID) =
        UriComponentsBuilder.newInstance().uri(URI.create(url))
            .queryParam(ORIGINAL_OPERATION_ID, paymentId)
            .queryParam(OPERATION_TYPE, PAYMENT)
            .build()
            .toUri()

    companion object : CompanionLogger() {
        private const val OPERATION_TYPE = "operationType"
        private const val ORIGINAL_OPERATION_ID = "originalOperationId"
    }
    */
}
