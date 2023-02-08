package com.menta.apiacquirers.application.port.out

import com.menta.apiacquirers.adapter.out.OperationPath
import com.menta.apiacquirers.adapter.out.OperationPathProvider
import com.menta.apiacquirers.domain.OperationType.PAYMENTS
import com.menta.apiacquirers.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class OperationPathSpec : FeatureSpec({
    feature("provide an operation path") {

        scenario("path provided") {
            val payment = OperationPath("payments", "/payments")
            val refund = OperationPath("refunds", "/refunds")
            val operationPathProvider = OperationPathProvider(listOf(payment, refund))

            operationPathProvider.provideBy(PAYMENTS) shouldBeRight "/payments"
        }

        scenario("path not provided") {

            val refund = OperationPath("refunds", "/refunds")
            val operationPathProvider = OperationPathProvider(listOf(refund))

            operationPathProvider.provideBy(PAYMENTS) shouldBeLeft ApplicationError.missingPathForOperation(PAYMENTS)
        }
    }
})
