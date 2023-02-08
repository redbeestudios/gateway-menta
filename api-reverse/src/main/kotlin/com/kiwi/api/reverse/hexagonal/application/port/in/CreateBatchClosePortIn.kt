package com.kiwi.api.reverse.hexagonal.application.port.`in`

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.reverse.hexagonal.domain.BatchClose

interface CreateBatchClosePortIn {
    fun execute(request: BatchCloseRequest, merchantId: String): BatchClose
}
