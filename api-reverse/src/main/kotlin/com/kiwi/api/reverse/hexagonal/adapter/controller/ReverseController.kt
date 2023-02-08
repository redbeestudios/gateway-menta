package com.kiwi.api.reverse.hexagonal.adapter.controller

import com.kiwi.api.reverse.hexagonal.adapter.controller.mapper.ResponseMapper
import com.kiwi.api.reverse.hexagonal.adapter.controller.model.*
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reverse.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reverse.hexagonal.domain.Annulment
import com.kiwi.api.reverse.hexagonal.domain.BatchClose
import com.kiwi.api.reverse.hexagonal.domain.Payment
import com.kiwi.api.reverse.hexagonal.domain.Refund
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import com.kiwi.api.reverse.shared.util.log.benchmark
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/reverse")
class ReverseController(
    private val createAnnulmentPortIn: CreateAnnulmentPortIn,
    private val createRefundPortIn: CreateRefundPortIn,
    private val createPaymentPortIn: CreatePaymentPortIn,
    private val createBatchClosePortIn: CreateBatchClosePortIn,
    private val responseMapper: ResponseMapper
) {

    @PostMapping("/payments")
    @ResponseStatus(CREATED)
    fun createPayment(
        @RequestHeader("merchantId") merchantId: String,
        @RequestBody request: PaymentRequest
    ): PaymentResponse =
        log.benchmark("create payment") {
            createPaymentFrom(request, merchantId)
                .toResponse()
        }

    @PostMapping("/annulments")
    @ResponseStatus(CREATED)
    fun createAnnulment(
        @RequestHeader("merchantId") merchantId: String,
        @RequestBody request: ReimbursementRequest
    ): ReimbursementResponse =
        log.benchmark("create annulment") {
            createAnnulmentFrom(request, merchantId)
                .toResponse()
        }

    @PostMapping("/refunds")
    @ResponseStatus(CREATED)
    fun createRefund(
        @RequestHeader("merchantId") merchantId: String,
        @RequestBody request: ReimbursementRequest
    ): ReimbursementResponse =
        log.benchmark("create refund") {
            createRefundFrom(request, merchantId)
                .toResponse()
        }

    @PostMapping("/batch-closes")
    @ResponseStatus(CREATED)
    fun createBatchClose(
        @RequestHeader("merchantId") merchantId: String,
        @RequestBody request: BatchCloseRequest
    ): BatchCloseResponse =
        log.benchmark("create batch close") {
            createBatchCloseFrom(request, merchantId)
                .toResponse()
        }

    private fun createPaymentFrom(request: PaymentRequest, merchantId: String) =
        createPaymentPortIn.execute(request, merchantId)
            .log { info("payment created: {}", it.id) }

    private fun createAnnulmentFrom(request: ReimbursementRequest, merchantId: String) =
        createAnnulmentPortIn.execute(request, merchantId)
            .log { info("annulment created: {}", it.id) }

    private fun createRefundFrom(request: ReimbursementRequest, merchantId: String) =
        createRefundPortIn.execute(request, merchantId)
            .log { info("refund created: {}", it.id) }

    private fun createBatchCloseFrom(request: BatchCloseRequest, merchantId: String) =
        createBatchClosePortIn.execute(request, merchantId)
            .log { info("payment created: {}", it.id) }

    private fun Payment.toResponse() =
        responseMapper.map(this)
            .log { info("response mapped for payment: {}", it.id) }

    private fun Annulment.toResponse() =
        responseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    private fun Refund.toResponse() =
        responseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    private fun BatchClose.toResponse() =
        responseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    companion object : CompanionLogger()
}
