package com.menta.apiacquirers.adapter.`in`

import com.fasterxml.jackson.databind.JsonNode
import com.menta.apiacquirers.application.port.`in`.CreateOperationInPort
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.domain.OperationType.ANNULMENTS
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.domain.OperationType.REFUNDS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_ANNULMENTS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_PAYMENTS
import com.menta.apiacquirers.domain.OperationType.REVERSAL_REFUNDS
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.log.benchmark
import com.menta.apiacquirers.shared.util.rest.evaluate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/private")
class OperationController(
    private val createOperationInPort: CreateOperationInPort,
    private val errorResponseProvider: ErrorResponseProvider
) {
    @PostMapping("/payments")
    fun createPayment(@RequestBody operation: JsonNode, @RequestHeader country: String): ResponseEntity<out Any> =
        createOperation(operation, country, PAYMENTS)

    @PostMapping("/annulments")
    fun createAnnulment(@RequestBody operation: JsonNode, @RequestHeader country: String): ResponseEntity<out Any> =
        createOperation(operation, country, ANNULMENTS)

    @PostMapping("/refunds")
    fun createRefund(@RequestBody operation: JsonNode, @RequestHeader country: String): ResponseEntity<out Any> =
        createOperation(operation, country, REFUNDS)

    @PostMapping("/reversals/payments")
    fun createPaymentReversal(
        @RequestBody operation: JsonNode,
        @RequestHeader country: String
    ): ResponseEntity<out Any> =
        createOperation(operation, country, REVERSAL_PAYMENTS)

    @PostMapping("/reversals/annulments")
    fun createAnnulmentReversal(
        @RequestBody operation: JsonNode,
        @RequestHeader country: String
    ): ResponseEntity<out Any> =
        createOperation(operation, country, REVERSAL_ANNULMENTS)

    @PostMapping("/reversals/refunds")
    fun createRefundReversal(
        @RequestBody operation: JsonNode,
        @RequestHeader country: String
    ): ResponseEntity<out Any> =
        createOperation(operation, country, REVERSAL_REFUNDS)

    private fun createOperation(operation: JsonNode, country: String, operationType: OperationType) =
        log.benchmark("proxy $operationType") {
            createOperationInPort
                .execute(operation, country, operationType)
                .evaluate { errorResponseProvider.provideFor(this) }
                .log { info("operation response: {}", it.statusCode) }
        }

    companion object : CompanionLogger()
}
