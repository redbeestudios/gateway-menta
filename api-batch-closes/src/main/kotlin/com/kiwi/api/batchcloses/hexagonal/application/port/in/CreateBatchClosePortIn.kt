package com.kiwi.api.batchcloses.hexagonal.application.port.`in`

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose

interface CreateBatchClosePortIn {
    fun execute(request: BatchCloseRequest, merchantId: String): BatchClose
}
