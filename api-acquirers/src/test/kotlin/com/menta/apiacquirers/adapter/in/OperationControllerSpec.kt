package com.menta.apiacquirers.adapter.`in`

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.menta.apiacquirers.application.port.`in`.CreateOperationInPort
import com.menta.apiacquirers.domain.OperationType.ANNULMENTS
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.domain.OperationType.REFUNDS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_ANNULMENTS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_PAYMENTS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_REFUNDS
import com.menta.apiacquirers.shared.error.anApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity

class OperationControllerSpec : FeatureSpec({

    val inPort = mockk<CreateOperationInPort>()
    val errorResponseProvider = mockk<ErrorResponseProvider>()
    val controller = OperationController(inPort, errorResponseProvider)

    beforeEach { clearAllMocks() }

    feature("create payment") {

        scenario("payment response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, PAYMENTS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createPayment(operation, country) shouldBe entity

            verify(exactly = 1) { inPort.execute(operation, country, PAYMENTS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating payment") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, PAYMENTS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createPayment(operation, country) shouldBe ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, PAYMENTS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }

    feature("create refund") {

        scenario("refund response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, REFUNDS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createRefund(operation, country) shouldBe entity

            verify(exactly = 1) { inPort.execute(operation, country, REFUNDS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating refund") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, REFUNDS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createRefund(operation, country) shouldBe ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, REFUNDS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }

    feature("create annulments") {

        scenario("annulments response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, ANNULMENTS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createAnnulment(operation, country) shouldBe entity

            verify(exactly = 1) { inPort.execute(operation, country, ANNULMENTS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating annulments") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, ANNULMENTS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createAnnulment(operation, country) shouldBe ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, ANNULMENTS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }

    feature("create payment reversals") {

        scenario("payment reversals response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, REVERSAL_PAYMENTS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createPaymentReversal(operation, country) shouldBe
                entity

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_PAYMENTS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating payment reversals") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, REVERSAL_PAYMENTS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createPaymentReversal(operation, country) shouldBe
                ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_PAYMENTS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }

    feature("create refund reversal") {

        scenario("refund reversal response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, REVERSAL_REFUNDS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createRefundReversal(operation, country) shouldBe entity

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_REFUNDS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating refund reversal") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, REVERSAL_REFUNDS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createRefundReversal(operation, country) shouldBe ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_REFUNDS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }

    feature("create annulment reversal") {

        scenario("annulments reversal response obtained") {
            val operation = IntNode(123)
            val country = "a country"
            val response = IntNode(234)
            val entity = ResponseEntity(response, CREATED)

            every { inPort.execute(operation, country, REVERSAL_ANNULMENTS) } returns
                entity.right() as Either<ApplicationError, ResponseEntity<JsonNode>>

            controller.createAnnulmentReversal(operation, country) shouldBe entity

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_ANNULMENTS) }
            verify(exactly = 0) { errorResponseProvider.provideFor(any()) }
        }

        scenario("error creating annulment reversal") {
            val operation = IntNode(123)
            val country = "a country"
            val response = acquirerNotFound("an acquirer")
            val apiErrorResponse = anApiErrorResponse()

            every { inPort.execute(operation, country, REVERSAL_ANNULMENTS) } returns
                response.left() as Either<ApplicationError, ResponseEntity<JsonNode>>

            every { errorResponseProvider.provideFor(response) } returns apiErrorResponse

            controller.createAnnulmentReversal(operation, country) shouldBe ResponseEntity(apiErrorResponse, response.status)

            verify(exactly = 1) { inPort.execute(operation, country, REVERSAL_ANNULMENTS) }
            verify(exactly = 1) { errorResponseProvider.provideFor(any()) }
        }
    }
})
