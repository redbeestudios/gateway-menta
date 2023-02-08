package com.kiwi.api.payments.hexagonal.adapter.port.`in`.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.consumer.PaymentEventConsumer
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.payments.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class PaymentEventConsumerSpec : FeatureSpec({

    val createOperation: CreateOperationInPort = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
    val consumer = PaymentEventConsumer(createOperation, consumerMessageResolver)

    feature("consume event") {

        scenario("successful consume") {
            val message = "a message"
            val operation = aCreatedPayment()
            val topic = "a topic"

            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns operation
            every { createOperation.execute(operation) } returns Unit

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { createOperation.execute(operation) }
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
