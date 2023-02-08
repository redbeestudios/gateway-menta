package com.menta.api.transactions.adapter.`in`.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aCreatedBillPaymentMessageCash
import com.menta.api.transactions.aCreatedBillPaymentMessageDebit
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.domain.OperationType
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

class BillPaymentEventConsumerSpec : FeatureSpec({
    val createPaymentTransaction: CreateTransactionInPort = mockk()
    val mapper: ToTransactionMapper = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val consumer = BillPaymentEventConsumer(
        createTransaction = createPaymentTransaction,
        toDomainMapper = mapper,
        consumerMessageResolver = consumerMessageResolver
    )

    beforeEach { clearAllMocks() }

    feature("consume event") {
        scenario("successful consume cash") {
            val id = UUID.fromString(TRANSACTION_ID)
            val transaction = aTransaction(id)
            val message = aCreatedBillPaymentMessageCash()
            val serializedMessage = "a message"

            // given mocked dependencies
            every { mapper.map(message, OperationType.PAYMENT) } returns transaction
            every { createPaymentTransaction.execute(transaction) } returns id
            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message

            // expect that
            consumer.consume(serializedMessage, "a topic", ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { mapper.map(message, OperationType.PAYMENT) }
            verify(exactly = 1) { createPaymentTransaction.execute(transaction) }
            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
        }

        scenario("successful consume debit") {
            val id = UUID.fromString(TRANSACTION_ID)
            val transaction = aTransaction(id)
            val message = aCreatedBillPaymentMessageDebit()
            val serializedMessage = "a message"

            // given mocked dependencies
            every { mapper.map(message, OperationType.PAYMENT) } returns transaction
            every { createPaymentTransaction.execute(transaction) } returns id
            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message

            // expect that
            consumer.consume(serializedMessage, "a topic", ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { mapper.map(message, OperationType.PAYMENT) }
            verify(exactly = 1) { createPaymentTransaction.execute(transaction) }
            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
        }
    }
})
