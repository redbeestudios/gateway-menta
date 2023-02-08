package com.kiwi.api.payments.hexagonal.adapter.`in`.controller

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentResponseMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.provider.PaymentProvider
import com.kiwi.api.payments.hexagonal.application.port.`in`.CreateBillPaymentPortIn
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import com.kiwi.api.payments.shared.util.rest.throwIfLeft
import org.springframework.http.HttpStatus.CREATED
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class BillPaymentController(
    private val createBillPaymentPortIn: CreateBillPaymentPortIn,
    private val toPaymentResponseMapper: ToPaymentResponseMapper,
    private val paymentProvider: PaymentProvider
) {

    @PreAuthorize("hasAuthority('Payment::Create')")
    @PostMapping("/public/payments/bills")
    @ResponseStatus(CREATED)
    fun create(
        @RequestBody request: PaymentRequest
    ) =
        log.benchmark("create bill payment") {
            toDomain(request)
                .doCreateFrom()
                .map { buildResponse(it) }
                .throwIfLeft()
        }

    private fun toDomain(paymentRequest: PaymentRequest) =
        paymentProvider.provide(paymentRequest)

    private fun Payment.doCreateFrom() =
        createBillPaymentPortIn.execute(this)
            .log { info("payment created: {}", it) }

    private fun buildResponse(createdPayment: CreatedPayment) =
        toPaymentResponseMapper.map(createdPayment)
            .log { info("Response for created payment: {}", it.id) }

    companion object : CompanionLogger()
}
