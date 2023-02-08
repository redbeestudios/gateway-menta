package com.kiwi.api.batchcloses.hexagonal.application.usecase

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.domain.Authorization
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
import com.kiwi.api.batchcloses.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class BuildBatchCloseUseCase {

    fun buildFrom(request: BatchCloseRequest, authorization: Authorization, merchantId: String, id: String) =
        with(request) {
            BatchClose(
                id = id,
                authorization = authorization,
                merchant = BatchClose.Merchant(
                    id = merchantId
                ),
                terminal = BatchClose.Terminal(
                    id = "123",
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                ticket = ticket,
                trace = trace,
                batch = batch,
                hostMessage = hostMessage,
                datetime = datetime,
                totals = totals.map {
                    BatchClose.Total(
                        operationCode = it.operationCode,
                        amount = it.amount,
                        currency = it.currency
                    )
                }
            )
        }
            .log { info("batch close built: {}", it) }

    companion object : CompanionLogger()
}
