package com.menta.api.feenicia.adapter.rest.mapper

import com.menta.api.feenicia.adapter.rest.model.FeeniciaRefundRequest
import com.menta.api.feenicia.domain.Operation
import org.springframework.stereotype.Component

@Component
class ToFeeniciaRefundRequestMapper {
    fun map(operation: Operation) =
        with(operation) {
            FeeniciaRefundRequest(
                transactionId = transactionId!!,
                transactionDate = transactionDate
            )
        }
}
