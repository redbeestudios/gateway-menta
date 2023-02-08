package com.kiwi.api.payments.adapter.controller

import com.kiwi.api.payments.adapter.controller.PaymentController.Companion.log
import com.kiwi.api.payments.adapter.controller.mapper.ToOperationMapper
import com.kiwi.api.payments.adapter.controller.mapper.ToReimbursementResponseMapper
import com.kiwi.api.payments.adapter.controller.models.ReimbursementRequest
import com.kiwi.api.payments.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerCustomerInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerMerchantInPort
import com.kiwi.api.payments.application.port.`in`.FindAcquirerTerminalInPort
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.OperationType.ANNULMENT
import com.kiwi.api.payments.domain.OperationType.REFUND
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import com.kiwi.api.payments.shared.util.rest.evaluate
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
@RequestMapping("/private")
class ReimbursementController(
    private val createOperationInPort: CreateOperationInPort,
    private val toReimbursementResponseMapper: ToReimbursementResponseMapper,
    private val toOnlineOperationMapper: ToOperationMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val findAcquirerCustomer: FindAcquirerCustomerInPort,
    private val findAcquirerMerchantInPort: FindAcquirerMerchantInPort,
    private val findAcquirerTerminalInPort: FindAcquirerTerminalInPort
) {

    @PostMapping("/annulments")
    @ResponseStatus(CREATED)
    fun createAnnulment(@RequestBody @Valid request: ReimbursementRequest) =
        log.benchmark("create annulment") {
            toDomain(request, ANNULMENT)
                .execute()
                .map { buildResponse(it, request) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    @PostMapping("/refunds")
    @ResponseStatus(CREATED)
    fun createRefund(@RequestBody @Valid request: ReimbursementRequest) =
        log.benchmark("create refund") {
            toDomain(request, REFUND)
                .execute()
                .map { buildResponse(it, request) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    private fun toDomain(request: ReimbursementRequest, operationType: OperationType) =
        request.let {
            runBlocking {
                val acquirerCustomer = async { it.findAcquirerCustomer() }
                val acquirerMerchant = async { it.findAcquirerMerchant() }
                val acquirerTerminal = async { it.findAcquirerTerminal() }

                toOnlineOperationMapper.map(
                    it,
                    operationType,
                    acquirerCustomer.await(),
                    acquirerMerchant.await(),
                    acquirerTerminal.await()
                ).log {
                    info("Reimbursement mapped for request to acquirer")
                }
            }
        }

    suspend fun ReimbursementRequest.findAcquirerCustomer() =
        findAcquirerCustomer.execute(this.customer.id)

    suspend fun ReimbursementRequest.findAcquirerMerchant() =
        findAcquirerMerchantInPort.execute(this.merchant.id)

    suspend fun ReimbursementRequest.findAcquirerTerminal() =
        findAcquirerTerminalInPort.execute(this.terminal.id)

    private fun Operation.execute() =
        createOperationInPort.execute(this)

    private fun buildResponse(createdOnlineOperation: CreatedOperation, request: ReimbursementRequest) =
        toReimbursementResponseMapper.map(createdOnlineOperation, request)

    companion object : CompanionLogger()
}
