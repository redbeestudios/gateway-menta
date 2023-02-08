package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reversal.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class RefundEventConsumerSpec : FeatureSpec({

    val createRefundPortIn: CreateRefundPortIn = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
    val consumer = RefundEventConsumer(createRefundPortIn, consumerMessageResolver)

    beforeEach { clearAllMocks() }

    feature("consume refund event") {
        scenario("successful consume") {
            val message = "a message"
            val refund = aRefund()
            val createdRefund = aCreatedRefund()
            val topic = "a topic"

            // given mocked dependencies
            every { objectMapper.readValue(message, any< TypeReference<*>>()) } returns refund
            every { createRefundPortIn.execute(refund) } returns createdRefund.right()

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { createRefundPortIn.execute(refund) }
            verify(exactly = 1) { objectMapper.readValue(message, any< TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
