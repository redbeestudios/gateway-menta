package com.kiwi.api.batchcloses.hexagonal.adapter.controller

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseResponse
import com.kiwi.api.batchcloses.hexagonal.adapter.controller.mapper.ToBatchCloseResponseMapper
import com.kiwi.api.batchcloses.hexagonal.application.usecase.crud.CreateBatchCloseUseCase
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
import com.kiwi.api.batchcloses.shared.util.log.CompanionLogger
import com.kiwi.api.batchcloses.shared.util.log.benchmark
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat

@RestController
class BatchCloseController(
    private val createBatchCloseUseCase: CreateBatchCloseUseCase,
    private val mapper: ToBatchCloseResponseMapper
) {

    @PostMapping("/public/batch-closes")
    @ResponseStatus(CREATED)
    fun create(
        @RequestHeader("merchantId") merchantId: String,
        @RequestBody request: BatchCloseRequest
    ): BatchCloseResponse =
        log.benchmark("create batch close") {

            createPaymentFrom(request, merchantId)
                .toResponse()
        }

    private fun createPaymentFrom(request: BatchCloseRequest, merchantId: String) =
        createBatchCloseUseCase.execute(request, merchantId)
            .log { info("batch close created: {}", it.id) }

    private fun BatchClose.toResponse() =
        mapper.map(this)
            .log { info("response mapped for batch close: {}", it.id) }

    companion object : CompanionLogger()
}
