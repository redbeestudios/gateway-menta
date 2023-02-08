package com.kiwi.api.reversal.hexagonal.application.usecase.operations

import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateBatchClosePortIn
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateBatchCloseUseCase() : CreateBatchClosePortIn {

    override fun execute(batchClose: BatchClose): BatchClose = batchClose

    companion object : CompanionLogger()
}
