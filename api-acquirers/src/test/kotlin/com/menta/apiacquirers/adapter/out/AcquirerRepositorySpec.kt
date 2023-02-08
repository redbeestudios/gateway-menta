package com.menta.apiacquirers.adapter.out

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.menta.apiacquirers.adapter.out.provider.AcquirerAwareUriProvider
import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForAcquirer
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.unknownRepositoryError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec
import reactor.core.publisher.Mono
import java.net.URI

class AcquirerRepositorySpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val uriProvider = mockk<AcquirerAwareUriProvider>()

    val requestBodyUriSpec = mockk<RequestBodyUriSpec>()
    val requestBodySpec = mockk<RequestBodySpec>()
    val requestHeaderSpec = mockk<RequestHeadersSpec<*>>()
    val responseSpec = mockk<ResponseSpec>()

    val repository = AcquirerRepository(
        webClient = webClient,
        uriProvider = uriProvider
    )

    beforeEach { clearAllMocks() }

    feature("exchange operation") {

        scenario("operation exchanged") {
            val operation = IntNode(123)
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a country")
            val operationType = PAYMENTS
            val uri = URI("")

            val response = IntNode(234)
            val responseEntity = ResponseEntity(response, OK)

            every { uriProvider.provideFor(acquirer, operationType) } returns uri.right()
            every { webClient.post() } returns requestBodyUriSpec
            every { requestBodyUriSpec.uri(uri) } returns requestBodySpec
            every { requestBodySpec.body(any(), JsonNode::class.java) } returns requestHeaderSpec
            every { requestHeaderSpec.retrieve() } returns responseSpec
            every { responseSpec.onStatus(any(), any()) } returns responseSpec
            every { responseSpec.toEntity(JsonNode::class.java) } returns Mono.just(responseEntity) as Mono<ResponseEntity<JsonNode>>

            repository.execute(operation, acquirer, operationType) shouldBeRight responseEntity

            verify(exactly = 1) { uriProvider.provideFor(acquirer, operationType) }
            verify(exactly = 1) { webClient.post() }
        }

        scenario("failed operation exchanged") {
            val operation = IntNode(123)
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a country")
            val operationType = PAYMENTS
            val uri = URI("")

            val response = IntNode(234)
            val responseEntity = ResponseEntity(response, OK)

            val exception =
                RepositoryException(responseEntity as ResponseEntity<JsonNode>)

            every { uriProvider.provideFor(acquirer, operationType) } returns uri.right()
            every { webClient.post() } returns requestBodyUriSpec
            every { requestBodyUriSpec.uri(uri) } returns requestBodySpec
            every { requestBodySpec.body(any(), JsonNode::class.java) } returns requestHeaderSpec
            every { requestHeaderSpec.retrieve() } returns responseSpec
            every { responseSpec.onStatus(any(), any()) } returns responseSpec
            every { responseSpec.toEntity(JsonNode::class.java) } returns Mono.error(exception)

            repository.execute(operation, acquirer, operationType) shouldBeRight responseEntity

            verify(exactly = 1) { uriProvider.provideFor(acquirer, operationType) }
            verify(exactly = 1) { webClient.post() }
        }

        scenario("empty response") {
            val operation = IntNode(123)
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a country")
            val operationType = PAYMENTS
            val uri = URI("")
            val mono = mockk<Mono<ResponseEntity<JsonNode>>>()

            every { uriProvider.provideFor(acquirer, operationType) } returns uri.right()
            every { webClient.post() } returns requestBodyUriSpec
            every { requestBodyUriSpec.uri(uri) } returns requestBodySpec
            every { requestBodySpec.body(any(), JsonNode::class.java) } returns requestHeaderSpec
            every { requestHeaderSpec.retrieve() } returns responseSpec
            every { responseSpec.onStatus(any(), any()) } returns responseSpec
            every { responseSpec.toEntity(JsonNode::class.java) } returns mono
            every { mono.block() } returns null

            repository.execute(operation, acquirer, operationType) shouldBeLeft unknownRepositoryError()

            verify(exactly = 1) { uriProvider.provideFor(acquirer, operationType) }
            verify(exactly = 1) { webClient.post() }
        }

        scenario("error providing uri") {
            val operation = IntNode(123)
            val acquirer = OperableAcquirers.Acquirer("an acquirer", "a country")
            val operationType = PAYMENTS
            val uriError = missingPathForAcquirer(acquirer.name)

            every { uriProvider.provideFor(acquirer, operationType) } returns uriError.left()

            repository.execute(operation, acquirer, operationType) shouldBeLeft uriError

            verify(exactly = 1) { uriProvider.provideFor(acquirer, operationType) }
            verify(exactly = 0) { webClient.post() }
        }
    }
})
