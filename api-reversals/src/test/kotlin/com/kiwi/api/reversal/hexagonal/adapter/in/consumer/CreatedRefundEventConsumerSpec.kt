package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reversal.hexagonal.application.aConsumerMessage
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateOperationInPort
import com.kiwi.api.reversal.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class CreatedRefundEventConsumerSpec : FeatureSpec({

    val createOperation: CreateOperationInPort = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val consumer = CreatedRefundEventConsumer(createOperation, consumerMessageResolver)

    feature("consume refund event") {
        scenario("successful consume") {
            val message = "a message"
            val operation = aCreatedRefund()
            val consumerMessage = aConsumerMessage(operation)

            // given mocked dependencies
            every { createOperation.execute(operation) } returns Unit
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns operation

            // expect that
            consumer.consume(message, consumerMessage.topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { createOperation.execute(operation) }
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
        }
    }
})
