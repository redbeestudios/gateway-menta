package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest

import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import java.util.UUID

class TransactionRestClientSpec : FeatureSpec({

    feature("find transaction") {

        scenario("mock test") {

            TransactionRestClient().retrieve(UUID.randomUUID()) shouldBeRight
                Transaction(UUID.fromString("48394174-2b3a-4875-ba40-6ece6209c1b2"))
        }
    }

/*
    feature("find transaction") {
        val mapper = mockk<ToTransactionMapper>()
        val webClient = mockk<WebClient>()
        val timeout = 500L
        val url = "http://localhost:8080/public/transaction"

        val repository = TransactionRestClient(
            toTransactionMapper = mapper,
            webClient = webClient,
            url = url,
            timeout = timeout
        )

        val spec = mockk<WebClient.RequestHeadersUriSpec<*>>()
        val responseSpec = mockk<WebClient.ResponseSpec>()
        beforeEach { clearAllMocks() }

        scenario("transaction found") {
            val paymentId = paymentId
            val response = aTransactionResponse()
            val transaction = aTransaction()
            val uri = URI("$url?originalOperationId=$paymentId&operationType=PAYMENT")

            every { webClient.get() } returns spec
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(TransactionResponse::class.java) } returns Mono.just(response)
            every { mapper.map(response) } returns transaction

            repository.retrieve(paymentId) shouldBeRight transaction

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(TransactionResponse::class.java) }
            verify(exactly = 1) { mapper.map(response) }
        }

        scenario("client returned 404") {
            val paymentId = paymentId
            val response = aTransactionResponse()
            val uri = URI("$url?originalOperationId=$paymentId&operationType=PAYMENT")
            val error = WebClientResponseException(404, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(TransactionResponse::class.java) } returns Mono.error(error)

            repository.retrieve(paymentId) shouldBeLeft transactionNotFound(paymentId.toString())

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(TransactionResponse::class.java) }
            verify(exactly = 0) { mapper.map(response) }
        }

        scenario("client returned error") {
            val paymentId = paymentId
            val response = aTransactionResponse()
            val uri = URI("$url?originalOperationId=$paymentId&operationType=PAYMENT")
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(TransactionResponse::class.java) } returns Mono.error(error)

            repository.retrieve(paymentId) shouldBeLeft transactionRepositoryError()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(TransactionResponse::class.java) }
            verify(exactly = 0) { mapper.map(response) }
        }

        scenario("timeout") {
            val paymentId = paymentId
            val response = aTransactionResponse()
            val uri = URI("$url?originalOperationId=$paymentId&operationType=PAYMENT")
            val error = WebClientResponseException(408, "a status text", null, null, null)

            every { webClient.get() } returns spec
            every { spec.uri(uri) } returns spec
            every { spec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(TransactionResponse::class.java) } returns Mono.error(error)

            repository.retrieve(paymentId) shouldBeLeft ApplicationError.timeout()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { spec.uri(uri) }
            verify(exactly = 1) { spec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(TransactionResponse::class.java) }
            verify(exactly = 0) { mapper.map(response) }
        }
    }
*/
})
