package com.menta.api.transactions.adapter.`in`.consumer.mapper

import com.menta.api.transactions.TestConstants.Companion.REFUNDED_AMOUNT
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aCreatedAnnulment
import com.menta.api.transactions.aCreatedBillPaymentMessageCash
import com.menta.api.transactions.aCreatedBillPaymentMessageDebit
import com.menta.api.transactions.aCreatedPaymentMessage
import com.menta.api.transactions.aCreatedRefund
import com.menta.api.transactions.aReversalOperation
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.aTransactionBillCash
import com.menta.api.transactions.aTransactionBillDebit
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode.APPROVED
import com.menta.api.transactions.domain.StatusCode.REVERSED
import com.menta.api.transactions.domain.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class ToTransactionMapperSpec : FeatureSpec({

    val idProvider: IdProvider = mockk()
    val mapper = ToTransactionMapper(idProvider)

    feature("map payment message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val result = mapper.map(aCreatedPaymentMessage(), OperationType.PAYMENT)
        val transactionId = result.id
        scenario("successful mapping") {
            result shouldBe aTransaction(transactionId)
        }
    }

    feature("map refund message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val transaction = aTransaction(UUID.fromString(TRANSACTION_ID))
        val result = mapper.map(transaction, aCreatedRefund(APPROVED), OperationType.PAYMENT)
        val transactionId = result.id

        scenario("successful mapping") {
            result shouldBe aTransaction(transactionId).copy(refundedAmount = REFUNDED_AMOUNT)
        }
    }

    feature("map annulment message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val transaction = aTransaction(UUID.fromString(TRANSACTION_ID))
        val result = mapper.map(transaction, aCreatedAnnulment(), OperationType.PAYMENT)
        val transactionId = result.id

        scenario("successful mapping") {
            result shouldBe aTransaction(transactionId).copy(refundedAmount = REFUNDED_AMOUNT)
        }
    }

    feature("map bill-payment cash message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val result = mapper.map(aCreatedBillPaymentMessageCash(), OperationType.PAYMENT)
        val transactionId = result.id
        scenario("successful mapping") {
            result shouldBe aTransactionBillCash(transactionId)
        }
    }

    feature("map bill-payment debit message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val result = mapper.map(aCreatedBillPaymentMessageDebit(), OperationType.PAYMENT)
        val transactionId = result.id
        scenario("successful mapping") {
            result shouldBe aTransactionBillDebit(transactionId)
        }
    }

    feature("map reverse operation message") {

        every { idProvider.provide() } returns UUID.fromString(TRANSACTION_ID)

        val transaction = aTransaction(UUID.fromString(TRANSACTION_ID))
        val result = mapper.map(transaction, aReversalOperation())
        val transactionId = result.id
        scenario("successful mapping") {
            val transactionResult = aTransaction(transactionId)
            val operationReversed = transactionResult.operation.copy(status = REVERSED)
            result shouldBe transactionResult.copy(operation = operationReversed)
        }
    }
})
