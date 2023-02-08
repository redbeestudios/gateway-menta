package com.menta.apiacquirers.application.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.menta.apiacquirers.application.port.out.AcquirerRepositoryOutPort
import com.menta.apiacquirers.domain.OperableAcquirers.Acquirer
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.domain.provider.AcquirerProvider
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.unknownRepositoryError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CreateOperationUseCaseSpec : FeatureSpec({

    val acquirerProvider = mockk<AcquirerProvider>()
    val repository = mockk<AcquirerRepositoryOutPort>()

    val useCase = CreateOperationUseCase(
        acquirerProvider = acquirerProvider,
        acquirerRepositoryOutPort = repository
    )

    beforeEach { clearAllMocks() }

    feature("create operation") {

        scenario("acquirer not found") {
            val country = "a country"
            val operation = mockk<JsonNode>()
            val operationType = PAYMENTS
            val error = acquirerNotFound(country)

            // given mocked dependencies
            every { acquirerProvider.provideBy(country) } returns error.left()

            useCase.execute(operation, country, operationType) shouldBeLeft error
            verify(exactly = 1) { acquirerProvider.provideBy(country) }
            verify(exactly = 0) { repository.execute(any(), any(), any()) }
        }

        scenario("error with repository") {
            val country = "a country"
            val operation = mockk<JsonNode>()
            val operationType = PAYMENTS
            val acquirer = Acquirer("GPS", "ARS")
            val error = unknownRepositoryError()

            // given mocked dependencies
            every { acquirerProvider.provideBy(country) } returns acquirer.right()
            every { repository.execute(operation, acquirer, operationType) } returns error.left()

            useCase.execute(operation, country, operationType) shouldBeLeft error
            verify(exactly = 1) { acquirerProvider.provideBy(country) }
            verify(exactly = 1) { repository.execute(operation, acquirer, operationType) }
        }

        scenario("operation created") {
            val country = "a country"
            val operation = mockk<JsonNode>()
            val operationType = PAYMENTS
            val acquirer = Acquirer("GPS", "ARS")
            val response = ResponseEntity(IntNode(123), HttpStatus.OK)

            // given mocked dependencies
            every { acquirerProvider.provideBy(country) } returns acquirer.right()
            every { repository.execute(operation, acquirer, operationType) } returns response.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            useCase.execute(operation, country, operationType) shouldBeRight response
            verify(exactly = 1) { acquirerProvider.provideBy(country) }
            verify(exactly = 1) { repository.execute(operation, acquirer, operationType) }
        }
    }
})
