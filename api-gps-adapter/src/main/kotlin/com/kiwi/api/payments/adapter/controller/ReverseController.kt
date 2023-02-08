package com.kiwi.api.payments.adapter.controller

import com.kiwi.api.payments.adapter.controller.mapper.ToOperationMapper
import com.kiwi.api.payments.adapter.controller.mapper.ToOperationResponseMapper
import com.kiwi.api.payments.adapter.controller.models.OperationRequest
import com.kiwi.api.payments.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerCustomerInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerMerchantInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerTerminalInPort
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.OperationType.ANNULMENT_REVERSE
import com.kiwi.api.payments.domain.OperationType.PURCHASE_REVERSE
import com.kiwi.api.payments.domain.OperationType.REFUND_REVERSE
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import com.kiwi.api.payments.shared.util.rest.evaluate
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
    private val findAcquirerCustomer: FindAcquirerCustomerInPort,
    private val findAcquirerMerchantInPort: FindAcquirerMerchantInPort,
    private val findAcquirerTerminalInPort: FindAcquirerTerminalInPort
) {

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPaymentReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request, PURCHASE_REVERSE)

    @PostMapping("/annulments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAnnulmentReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request, ANNULMENT_REVERSE)

    @PostMapping("/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRefundReverse(@RequestBody @Valid request: OperationRequest) =
        executeOperation(request, REFUND_REVERSE)

    private fun executeOperation(request: OperationRequest, operationType: OperationType) =
        log.benchmark("create reverse") {
            toDomain(request, operationType)
                .execute()
                .map { buildResponse(it, request) }
                .evaluate(HttpStatus.CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun toDomain(request: OperationRequest, operationType: OperationType) =
        request.let {
            runBlocking {
                val acquirerCustomer = async { it.findAcquirerCustomer() }
                val acquirerMerchant = async { it.findAcquirerMerchant() }
                val acquirerTerminal = async { it.findAcquirerTerminal() }

                toOperationMapper.map(
                    it,
                    operationType,
                    acquirerCustomer.await(),
                    acquirerMerchant.await(),
                    acquirerTerminal.await()
                ).log {
                    info("Reversal mapped for request to acquirer")
                }
            }
        }

    suspend fun OperationRequest.findAcquirerCustomer() =
        findAcquirerCustomer.execute(this.customer.id)

    suspend fun OperationRequest.findAcquirerMerchant() =
        findAcquirerMerchantInPort.execute(this.merchant.id)

    suspend fun OperationRequest.findAcquirerTerminal() =
        findAcquirerTerminalInPort.execute(this.terminal.id)

    private fun Operation.execute() =
        createOperationInPort.execute(this)

    private fun buildResponse(createdOperation: CreatedOperation, request: OperationRequest) =
        toOperationResponseMapper.map(createdOperation, request)

    companion object : CompanionLogger()
}
