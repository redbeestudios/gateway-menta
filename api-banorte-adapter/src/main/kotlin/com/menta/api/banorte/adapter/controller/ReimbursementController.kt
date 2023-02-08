package com.menta.api.banorte.adapter.controller

import arrow.core.flatMap
import com.menta.api.banorte.adapter.controller.PaymentController.Companion.log
import com.menta.api.banorte.adapter.controller.mapper.ToOperationMapper
import com.menta.api.banorte.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.banorte.adapter.controller.models.OperationRequest
import com.menta.api.banorte.application.port.`in`.CreateOperationInPort
import com.menta.api.banorte.application.port.`in`.FindBanorteMerchantInPort
import com.menta.api.banorte.domain.CommandTransaction.REFUND
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.providers.ErrorResponseProvider
import com.menta.api.banorte.shared.util.log.CompanionLogger
import com.menta.api.banorte.shared.util.log.benchmark
import com.menta.api.banorte.shared.util.rest.evaluate
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/private")
class ReimbursementController(
    private val createOperationInPort: CreateOperationInPort,
    private val findBanorteMerchantInPort: FindBanorteMerchantInPort,
    private val toOperationResponseMapper: ToOperationResponseMapper,
    private val toOnlineOperationMapper: ToOperationMapper,
    private val errorResponseProvider: ErrorResponseProvider
) {
    @PostMapping("/refunds")
    @ResponseStatus(CREATED)
    fun createRefund(@RequestBody @Valid request: OperationRequest) =
        log.benchmark("create refund") {
            request
                .toDomain()
                .flatMap { it.execute() }
                .map { buildResponse(it, request) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun OperationRequest.toDomain() =
        this
            .findBanorteMerchant()
            .map { merchant ->
                toOnlineOperationMapper.map(this, merchant, REFUND)
                    .log { info("Request: {}", it) }
            }

    private fun Operation.execute() =
        createOperationInPort.execute(this)

    private fun buildResponse(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        toOperationResponseMapper.map(createdOperation, operationRequest)
            .log { info("Response: {}", it) }

    private fun OperationRequest.findBanorteMerchant() =
        findBanorteMerchantInPort.execute(this.merchant.id)

    companion object : CompanionLogger()
}
