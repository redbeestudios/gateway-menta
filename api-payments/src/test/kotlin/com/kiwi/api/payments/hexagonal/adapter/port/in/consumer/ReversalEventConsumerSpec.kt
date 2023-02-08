package com.kiwi.api.payments.hexagonal.adapter.port.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.consumer.ReversalEventConsumer
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import com.kiwi.api.payments.hexagonal.application.port.`in`.ReversePaymentPortIn
import com.kiwi.api.payments.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class ReversalEventConsumerSpec : FeatureSpec({
    feature("Produce reversal payment event") {
        lateinit var reversePaymentPortIn: ReversePaymentPortIn
        lateinit var consumer: ReversalEventConsumer
        lateinit var objectMapper: ObjectMapper
        lateinit var consumerMessageResolver: ConsumerMessageResolver
        lateinit var ack: Acknowledgment

        beforeEach {
            ack = mockk(relaxed = true)
            objectMapper = mockk()
            reversePaymentPortIn = mockk()
            consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
            consumer = ReversalEventConsumer(
                reversePaymentPortIn = reversePaymentPortIn,
                consumerMessageResolver = consumerMessageResolver
            )
        }

        scenario("successful consume") {
            val message = "a message"
            val aReversalOperation = aReversalOperation()
            val topic = "a topic"

            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns aReversalOperation
            every { reversePaymentPortIn.execute(aReversalOperation) } returns aReversalOperation.right()

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { reversePaymentPortIn.execute(aReversalOperation) }
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
