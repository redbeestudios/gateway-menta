package com.menta.api.feenicia.adapter.controller

import com.menta.api.feenicia.adapter.controller.mapper.ToOperationMapper
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.feenicia.adapter.controller.models.OperationRequest
import com.menta.api.feenicia.application.port.`in`.CreateOperationInPort
import com.menta.api.feenicia.application.port.`in`.FindFeeniciaMerchantInPort
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.domain.OperationType.REVERSAL
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import com.menta.api.feenicia.shared.util.log.benchmark
import com.menta.api.feenicia.shared.util.rest.evaluate
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/private/reversals")
class ReverseController(
    private val createOperationInPort: CreateOperationInPort,
    private val toOperationResponseMapper: ToOperationResponseMapper,
    private val toOperationMapper: ToOperationMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val findFeeniciaMerchantInPort: FindFeeniciaMerchantInPort
) {
    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPaymentReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request, PAYMENT)

    @PostMapping("/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRefundReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request, REFUND)

    private fun executeOperation(request: OperationRequest, operationType: OperationType) =
        log.benchmark("create reverse ${operationType.name}") {
            request.toDomain()
                .execute(operationType)
                .map { buildResponse(it, request) }
                .evaluate(HttpStatus.CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun OperationRequest.toDomain() =
        this.let {
            runBlocking {
                val feeniciaMerchant = async { it.findFeeniciaMerchant() }

                toOperationMapper.map(it, REVERSAL, feeniciaMerchant.await())
                    .log { info("Request: {}", it) }
            }
        }

    private suspend fun OperationRequest.findFeeniciaMerchant() =
        findFeeniciaMerchantInPort.execute(this.merchant.id)

    private fun Operation.execute(operationType: OperationType) =
        createOperationInPort.execute(this, operationType)

    private fun buildResponse(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        toOperationResponseMapper.map(createdOperation, operationRequest)
            .log { info("Response: {}", it) }

    companion object : CompanionLogger()
}
