package com.kiwi.api.reverse.hexagonal.application.port.`in`

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.domain.Annulment

interface CreateAnnulmentPortIn {
    fun execute(request: ReimbursementRequest, merchantId: String): Annulment
}
