package com.menta.api.feenicia.adapter.rest.provider

import com.menta.api.feenicia.domain.EntryMode.STRIPE
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.domain.OperationType.REVERSAL
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FeeniciaUrlProvider(
    @Value("\${api-feenicia-adapter.url.emv}")
    private val emv: String,
    @Value("\${api-feenicia-adapter.url.swipe}")
    private val swipe: String,
    @Value("\${api-feenicia-adapter.url.refund}")
    private val refund: String,
    @Value("\${api-feenicia-adapter.url.reversal}")
    private val reversal: String,
    @Value("\${api-feenicia-adapter.url.cancel}")
    private val cancel: String
) {
    fun provide(operation: Operation, operationType: OperationType): String =
        when (operationType) {
            PAYMENT -> buildEntryMode(operation)
            REFUND -> refund
            REVERSAL -> reversal
            else -> cancel
        }

    private fun buildEntryMode(operation: Operation) = when (operation.entryMode) {
        STRIPE -> swipe
        else -> emv
    }
}
