package com.kiwi.api.payments.hexagonal.adapter.port.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.consumer.CreatedReversalEventConsumer
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import com.kiwi.api.payments.hexagonal.application.port.`in`.UpdateStatusOperationPortIn
import com.kiwi.api.payments.shared.kafka.ConsumerMessageResolver
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class CreatedReversalEventConsumerSpec : FeatureSpec({
    feature("Produce reversal payment event") {

        lateinit var updateStatusOperationPortIn: UpdateStatusOperationPortIn
        lateinit var consumer: CreatedReversalEventConsumer
        lateinit var objectMapper: ObjectMapper
        lateinit var consumerMessageResolver: ConsumerMessageResolver
        lateinit var ack: Acknowledgment

        beforeEach {
            ack = mockk(relaxed = true)
            objectMapper = mockk()
            updateStatusOperationPortIn = mockk()
            consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
            consumer = CreatedReversalEventConsumer(
                updateStatusOperationPortIn = updateStatusOperationPortIn,
                consumerMessageResolver = consumerMessageResolver
            )
        }

        scenario("successful consume") {
            val message = "a message"
            val aReversalOperation = aReversalOperation()
            val topic = "a topic"
            val header = "PAYMENT_REVERSAL"

            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns aReversalOperation
            every { updateStatusOperationPortIn.execute(aReversalOperation) } returns Unit.right()

            // expect that
            consumer.consume(message, topic, header, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { updateStatusOperationPortIn.execute(aReversalOperation) }
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
