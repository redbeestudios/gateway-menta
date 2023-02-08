package com.menta.api.feenicia.adapter.controller

import com.menta.api.feenicia.adapter.controller.mapper.ToOperationMapper
import com.menta.api.feenicia.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.feenicia.adapter.controller.models.OperationRequest
import com.menta.api.feenicia.application.port.`in`.CreateOperationInPort
import com.menta.api.feenicia.application.port.`in`.FindFeeniciaMerchantInPort
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import com.menta.api.feenicia.shared.util.log.benchmark
import com.menta.api.feenicia.shared.util.rest.evaluate
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/private/payments")
class PaymentController(
    private val createOperationInPort: CreateOperationInPort,
    private val toOperationResponseMapper: ToOperationResponseMapper,
    private val toOperationMapper: ToOperationMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val findFeeniciaMerchantInPort: FindFeeniciaMerchantInPort
) {
    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@RequestBody @Valid request: OperationRequest) =
        log.benchmark("create call to acquirer for payment") {
            request
                .toDomain()
                .execute()
                .map { buildResponse(it, request) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun OperationRequest.toDomain() =
        this.let {
            runBlocking {
                val feeniciaMerchant = async { it.findFeeniciaMerchant() }

                toOperationMapper.map(it, PAYMENT, feeniciaMerchant.await())
                    .log { info("Request: {}", it) }
            }
        }

    private suspend fun OperationRequest.findFeeniciaMerchant() =
        findFeeniciaMerchantInPort.execute(this.merchant.id)

    private fun Operation.execute() =
        createOperationInPort.execute(this)

    private fun buildResponse(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        toOperationResponseMapper.map(createdOperation, operationRequest)
            .log { info("Response: {}", it) }

    companion object : CompanionLogger()
}
