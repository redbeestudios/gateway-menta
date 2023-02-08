package com.kiwi.api.reversal.hexagonal.application.port.`in`

import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose

interface CreateBatchClosePortIn {
    fun execute(batchClose: BatchClose): BatchClose
}
