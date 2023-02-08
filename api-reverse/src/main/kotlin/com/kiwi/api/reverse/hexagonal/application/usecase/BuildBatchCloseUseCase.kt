package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.hexagonal.domain.BatchClose
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class BuildBatchCloseUseCase {

    fun buildFrom(request: BatchCloseRequest, authorization: Authorization, id: String) =
        with(request) {
            BatchClose(
                id = id,
                authorization = authorization,
                trace = trace,
                batch = batch,
                hostMessage = hostMessage,
                terminal = BatchClose.Terminal(
                    id = "123",
                    serialCode = terminal.serialCode
                ),
                datetime = datetime,
                merchant = BatchClose.Merchant(
                    id = "merchantId"
                ),
                ticket = ticket,
                total = BatchClose.Total(
                    operationCode = total.operationCode,
                    amount = total.amount,
                    currency = total.currency
                ),
                softwareVersion = softwareVersion
            )
        }
            .log { info("BatchClose built: {}", it) }

    companion object : CompanionLogger()
}
