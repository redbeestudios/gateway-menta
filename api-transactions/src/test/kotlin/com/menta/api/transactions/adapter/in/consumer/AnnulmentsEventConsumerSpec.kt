package com.menta.api.transactions.adapter.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.transactions.TestConstants.Companion.PAYMENT_ID
import com.menta.api.transactions.TestConstants.Companion.REFUNDED_AMOUNT
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aCreatedAnnulment
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.domain.OperationType.ANNULMENT
import com.menta.api.transactions.domain.OperationType.PAYMENT
import com.menta.api.transactions.shared.kafka.ConsumerMessageResolver
import com.menta.api.transactions.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment
import java.util.UUID

class AnnulmentsEventConsumerSpec : FeatureSpec({
    val createTransactionInPort: CreateTransactionInPort = mockk()
    val findTransactionOperationInPort: FindTransactionOperationInPort = mockk()
    val mapper: ToTransactionMapper = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val consumer = AnnulmentsEventConsumer(
        createTransaction = createTransactionInPort,
        toDomainMapper = mapper,
        findTransactionOperationInPort = findTransactionOperationInPort,
        consumerMessageResolver = consumerMessageResolver
    )

    beforeEach { clearAllMocks() }

    feature("consume event") {
        scenario("successful consume") {
            val id = UUID.fromString(TRANSACTION_ID)
            val outDatedTransaction = aTransaction(id)
            val updateTransaction = aTransaction(id, REFUNDED_AMOUNT)
            val annulmentsMessage = aCreatedAnnulment()
            val serializedMessage = "a message"

            // given mocked dependencies
            every { findTransactionOperationInPort.execute(UUID.fromString(PAYMENT_ID), PAYMENT) } returns outDatedTransaction.right()
            every { mapper.map(outDatedTransaction, annulmentsMessage, ANNULMENT) } returns updateTransaction
            every { createTransactionInPort.execute(updateTransaction) } returns id
            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns annulmentsMessage

            // expect that
            consumer.consume(serializedMessage, "a topic", ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { mapper.map(outDatedTransaction, annulmentsMessage, ANNULMENT) }
            verify(exactly = 1) { findTransactionOperationInPort.execute(UUID.fromString(PAYMENT_ID), PAYMENT) }
            verify(exactly = 1) { createTransactionInPort.execute(updateTransaction) }
            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
        }
    }
})
