package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper.ToDomainMapper
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper.ToResponseMapper
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reversal.hexagonal.adapter.out.event.AnnulmentReversalProducer
import com.kiwi.api.reversal.hexagonal.adapter.out.event.PaymentReversalProducer
import com.kiwi.api.reversal.hexagonal.adapter.out.event.RefundReversalProducer
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reversal.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import com.kiwi.api.reversal.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import com.kiwi.api.reversal.shared.util.log.benchmark
import com.kiwi.api.reversal.shared.util.rest.evaluate
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.CREATED
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/public/reversals")
class ReversalController(
    private val createBatchClosePortIn: CreateBatchClosePortIn,
    private val errorResponseProvider: ErrorResponseProvider,
    private val toResponseMapper: ToResponseMapper,
    private val toDomainMapper: ToDomainMapper,
    private val findCustomerPortIn: FindCustomerPortIn,
    private val findTerminalPortIn: FindTerminalPortIn,
    private val findMerchantPortIn: FindMerchantPortIn,
    private val paymentReversalProducer: PaymentReversalProducer,
    private val annulmentReversalProducer: AnnulmentReversalProducer,
    private val refundReversalProducer: RefundReversalProducer
) {

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('PaymentReversal::Create')")
    @ResponseStatus(CREATED)
    fun createPayment(
        @RequestBody request: PaymentRequest
    ) =
        log.benchmark("create payment") {
            toDomainPayment(request)
                .inform()
                .map { buildResponse(it) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    @PostMapping("/annulments")
    @PreAuthorize("hasAuthority('AnnulmentReversal::Create')")
    @ResponseStatus(CREATED)
    fun createAnnulment(
        @RequestBody request: ReimbursementRequest
    ) =
        log.benchmark("create annulment") {
            toDomainAnnulment(request)
                .inform()
                .map { buildResponse(it) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    @PostMapping("/refunds")
    @PreAuthorize("hasAuthority('RefundReversal::Create')")
    @ResponseStatus(CREATED)
    fun createRefund(
        @RequestBody request: ReimbursementRequest
    ) =
        log.benchmark("create refund") {
            toDomainRefunds(request)
                .inform()
                .map { buildResponse(it) }
                .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
        }

    @PostMapping("/batch-closes")
    @PreAuthorize("hasAuthority('BatchCloseReversal::Create')")
    @ResponseStatus(CREATED)
    fun createBatchClose(
        @RequestBody request: BatchCloseRequest
    ): BatchCloseResponse =
        log.benchmark("create batch close") {
            toDomainBatchClose(request)
                .createBatchClose()
                .toResponse()
        }

    private fun toDomainPayment(request: PaymentRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toDomainMapper.mapToPayment(request, merchant.await(), customer.await(), it)
                    .log { info("payment created: {}", it) }
            }
        }

    private fun toDomainAnnulment(request: ReimbursementRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toDomainMapper.mapToAnnulment(request, merchant.await(), customer.await(), it)
                    .log { info("annulment created: {}", it) }
            }
        }

    private fun toDomainRefunds(request: ReimbursementRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toDomainMapper.mapToRefund(request, merchant.await(), customer.await(), it)
                    .log { info("refund created: {}", it) }
            }
        }

    private fun toDomainBatchClose(request: BatchCloseRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toDomainMapper.mapToBatchClose(request, merchant.await(), customer.await(), it)
                    .log { info("batch close created: {}", it) }
            }
        }

    private fun BatchClose.createBatchClose() =
        createBatchClosePortIn.execute(this)
            .log { info("payment created: {}", it.id) }

    private fun buildResponse(payment: Payment) =
        toResponseMapper.map(payment)
            .log { info("response mapped for payment: {}", it) }

    private fun buildResponse(annulment: Annulment) =
        toResponseMapper.map(annulment)
            .log { info("response mapped for annulment: {}", it) }

    private fun buildResponse(refund: Refund) =
        toResponseMapper.map(refund)
            .log { info("response mapped for refund: {}", it) }

    private fun BatchClose.toResponse() =
        toResponseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    private fun Refund.inform() =
        refundReversalProducer.produce(this)
            .logRight { info("refund reversal informed: {}", it) }
            .map { this }

    private fun Annulment.inform() =
        annulmentReversalProducer.produce(this)
            .logRight { info("annulment reversal informed: {}", it) }
            .map { this }

    private fun Payment.inform() =
        paymentReversalProducer.produce(this)
            .logRight { info("payment reversal informed: {}", it) }
            .map { this }

    private fun findTerminal(terminalId: UUID) =
        findTerminalPortIn.execute(terminalId)

    suspend fun ReceivedTerminal.findCustomer() =
        findCustomerPortIn.execute(customerId)

    suspend fun ReceivedTerminal.findMerchant() =
        findMerchantPortIn.execute(merchantId)

    companion object : CompanionLogger()
}
