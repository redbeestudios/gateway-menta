package com.kiwi.api.batchcloses.hexagonal.adapter.controller.mapper

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseResponse
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
import com.kiwi.api.batchcloses.shared.constants.OperationType
import com.kiwi.api.batchcloses.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToBatchCloseResponseMapper {

    fun map(batchClose: BatchClose) =
        with(batchClose) {
            BatchCloseResponse(
                id = id,
                status = BatchCloseResponse.Status(
                    code = authorization.status.code,
                    situation = authorization.status.situation?.let {
                        BatchCloseResponse.Status.Situation(
                            id = it.id,
                            description = it.description
                        )
                    }
                ),
                authorization = BatchCloseResponse.Authorization(
                    code = authorization.authorizationCode,
                    displayMessage = authorization.displayMessage,
                    retrievalReferenceNumber = authorization.retrievalReferenceNumber
                ),
                merchant = BatchCloseResponse.Merchant(
                    id = merchant.id
                ),
                terminal = BatchCloseResponse.Terminal(
                    id = terminal.id,
                    serialCode = terminal.serialCode,
                    softwareVersion = terminal.softwareVersion
                ),
                trace = trace,
                ticket = ticket,
                batch = ticket,
                hostMessage = hostMessage,
                datetime = datetime,
                totals = totals.map {
                    BatchCloseResponse.Total(
                        operationCode = it.operationCode,
                        amount = it.amount,
                        currency = it.currency
                    )
                }
            )
        }.log { info("batch close response mapped: {}", it) }

    companion object : CompanionLogger()
}
