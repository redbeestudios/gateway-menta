package com.kiwi.api.payments.hexagonal.adapter.port.out.event

import arrow.core.left
import com.kiwi.api.payments.hexagonal.adapter.out.event.CreatedPaymentProducer
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.application.port.out.CreatedPaymentProducerPortOut
import com.kiwi.api.payments.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class CreatedPaymentProducerSpec : FeatureSpec({
    feature("Produce created payment event ") {
        lateinit var eventProducer: CreatedPaymentProducerPortOut

        val kafkaTemplate: KafkaTemplate<String, String> = mockk(relaxed = true)
        val objectMapper = mockk<KafkaObjectMapper>()

        eventProducer = CreatedPaymentProducer(
            kafkaTemplate = kafkaTemplate,
            topic = "topic",
            objectMapper = objectMapper
        )

        beforeEach { clearAllMocks() }

        scenario("successful payment created send") {
            val message = aCreatedPayment()
            val serializedOperation = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(message) } returns serializedOperation

            // expect that
            eventProducer.produce(message)

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(message) }
            verify(exactly = 1) { kafkaTemplate.send("topic", message.id.toString(), serializedOperation) }
        }

        scenario("payment created created error") {
            val message = aCreatedPayment()
            val serializedOperation = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(message) } returns serializedOperation
            every { kafkaTemplate.send("topic", message.id.toString(), serializedOperation) } throws Exception()

            // expect that
            eventProducer.produce(message) shouldNotBeSameInstanceAs QueueProducerNotWritten().left()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(message) }
            verify(exactly = 1) { kafkaTemplate.send("topic", message.id.toString(), serializedOperation) }
        }
    }
})
