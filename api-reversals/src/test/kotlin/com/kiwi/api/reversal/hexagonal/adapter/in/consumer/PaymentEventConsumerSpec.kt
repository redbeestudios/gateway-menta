package com.kiwi.api.reversal.hexagonal.adapter.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.port.`in`.CreatePaymentPortIn
import com.kiwi.api.reversal.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class PaymentEventConsumerSpec : FeatureSpec({

    val createPaymentPortIn: CreatePaymentPortIn = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
    val consumer = PaymentEventConsumer(createPaymentPortIn, consumerMessageResolver)

    beforeEach { clearAllMocks() }

    feature("consume payment event") {
        scenario("successful consume") {
            val message = "a message"
            val payment = aPayment()
            val createdPayment = aCreatedPayment()
            val topic = "a topic"

            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns payment
            every { createPaymentPortIn.execute(payment) } returns createdPayment.right()

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { createPaymentPortIn.execute(payment) }
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
