package com.menta.api.banorte.adapter.controller

import arrow.core.flatMap
import com.menta.api.banorte.adapter.controller.mapper.ToOperationMapper
import com.menta.api.banorte.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.banorte.adapter.controller.models.OperationRequest
import com.menta.api.banorte.application.port.`in`.CreateOperationInPort
import com.menta.api.banorte.application.port.`in`.FindBanorteMerchantInPort
import com.menta.api.banorte.domain.CommandTransaction.REVERSAL
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.providers.ErrorResponseProvider
import com.menta.api.banorte.shared.util.log.CompanionLogger
import com.menta.api.banorte.shared.util.log.benchmark
import com.menta.api.banorte.shared.util.rest.evaluate
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
    private val findBanorteMerchantInPort: FindBanorteMerchantInPort,
    private val toOperationResponseMapper: ToOperationResponseMapper,
    private val toOperationMapper: ToOperationMapper,
    private val errorResponseProvider: ErrorResponseProvider
) {

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPaymentReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request)

    @PostMapping("/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRefundReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request)

    private fun executeOperation(request: OperationRequest) =
        log.benchmark("create reverse") {
            request
                .toDomain()
                .flatMap { it.execute() }
                .map { buildResponse(it, request) }
                .evaluate(HttpStatus.CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun OperationRequest.toDomain() =
        this
            .findBanorteMerchant()
            .map { merchant ->
                toOperationMapper.map(this, merchant, REVERSAL)
                    .log { info("Request: {}", it) }
            }

    private fun Operation.execute() =
        createOperationInPort.execute(this)

    private fun OperationRequest.findBanorteMerchant() =
        findBanorteMerchantInPort.execute(this.merchant.id)

    private fun buildResponse(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        toOperationResponseMapper.map(createdOperation, operationRequest)
            .log { info("Response: {}", it) }

    companion object : CompanionLogger()
}
