package com.kiwi.api.reverse.hexagonal.application.port.`in`

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.domain.Refund

interface CreateRefundPortIn {
    fun execute(request: ReimbursementRequest, merchantId: String): Refund
}
