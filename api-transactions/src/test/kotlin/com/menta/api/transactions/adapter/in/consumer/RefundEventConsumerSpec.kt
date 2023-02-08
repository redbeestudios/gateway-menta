package com.menta.api.transactions.adapter.`in`.consumer

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.transactions.aCreatedRefund
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedRefund
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.application.port.`in`.FindTransactionOperationInPort
import com.menta.api.transactions.domain.OperationType.PAYMENT
import com.menta.api.transactions.domain.OperationType.REFUND
import com.menta.api.transactions.domain.StatusCode.FAILED
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import com.menta.api.transactions.shared.kafka.ConsumerMessageResolver
import com.menta.api.transactions.shared.kafka.KafkaObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment
import java.util.UUID

class RefundEventConsumerSpec : FeatureSpec({
    val createTransactionInPort = mockk<CreateTransactionInPort>()
    val toDomainMapper = mockk<ToTransactionMapper>()
    val findTransactionOperationInPort = mockk<FindTransactionOperationInPort>()
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val ack = mockk<Acknowledgment>(relaxed = true)

    val consumer = RefundEventConsumer(
        createTransaction = createTransactionInPort,
        toDomainMapper = toDomainMapper,
        findTransactionOperationInPort = findTransactionOperationInPort,
        consumerMessageResolver = consumerMessageResolver
    )

    beforeEach { clearAllMocks() }

    feature("consume") {

        scenario("consume failed operation") {
            val serializedMessage = "a message"
            val message = aCreatedRefund(FAILED)

            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message

            consumer.consume(serializedMessage, "a topic", ack) shouldBe Unit

            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
            verify(exactly = 0) { createTransactionInPort.execute(any()) }
            verify(exactly = 0) { toDomainMapper.map(any(), any<CreatedRefund>(), any()) }
            verify(exactly = 0) { findTransactionOperationInPort.execute(any(), any()) }
        }

        scenario("original transaction not found") {
            val serializedMessage = "a message"
            val message = aCreatedRefund()

            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message
            every {
                findTransactionOperationInPort.execute(
                    UUID.fromString(message.data.paymentId),
                    PAYMENT
                )
            } returns operationNotFound("an acquirer id", PAYMENT).left()

            shouldThrow<ErrorFindingTransaction> {
                consumer.consume(serializedMessage, "a topic", ack)
            }

            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
            verify(exactly = 1) { findTransactionOperationInPort.execute(UUID.fromString(message.data.paymentId), PAYMENT) }
            verify(exactly = 0) { ack.acknowledge() }
            verify(exactly = 0) { createTransactionInPort.execute(any()) }
            verify(exactly = 0) { toDomainMapper.map(any(), any<CreatedRefund>(), any()) }
        }

        scenario("message consumed") {
            val serializedMessage = "a message"
            val message = aCreatedRefund()
            val transaction = aTransaction(UUID.randomUUID())
            val anotherTransaction = aTransaction(UUID.randomUUID())

            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message
            every { findTransactionOperationInPort.execute(UUID.fromString(message.data.paymentId), PAYMENT) } returns transaction.right()
            every { toDomainMapper.map(transaction, message, REFUND) } returns anotherTransaction
            every { createTransactionInPort.execute(anotherTransaction) } returns anotherTransaction.id

            consumer.consume(serializedMessage, "a topic", ack) shouldBe Unit

            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
            verify(exactly = 1) { findTransactionOperationInPort.execute(UUID.fromString(message.data.paymentId), PAYMENT) }
            verify(exactly = 1) { toDomainMapper.map(transaction, message, REFUND) }
            verify(exactly = 1) { createTransactionInPort.execute(anotherTransaction) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
