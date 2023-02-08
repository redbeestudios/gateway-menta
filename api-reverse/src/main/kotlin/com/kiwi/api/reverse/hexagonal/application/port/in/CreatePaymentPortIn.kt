package com.kiwi.api.reverse.hexagonal.application.port.`in`

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.PaymentRequest
import com.kiwi.api.reverse.hexagonal.domain.Payment

interface CreatePaymentPortIn {
    fun execute(paymentRequest: PaymentRequest, merchantId: String): Payment
}
