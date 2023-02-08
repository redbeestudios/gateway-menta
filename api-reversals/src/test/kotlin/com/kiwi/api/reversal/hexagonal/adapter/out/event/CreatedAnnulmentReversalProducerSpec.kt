package com.kiwi.api.reversal.hexagonal.adapter.out.event

import arrow.core.left
import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.shared.error.model.QueueProducerNotWritten
import com.kiwi.api.reversal.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class CreatedAnnulmentReversalProducerSpec : FeatureSpec({
    feature("Produce created payment event ") {

        val kafkaTemplate: KafkaTemplate<String, String> = mockk()
        val objectMapper = mockk<KafkaObjectMapper>()
        val eventProducer = CreatedAnnulmentReversalProducer(
            kafkaTemplate = kafkaTemplate,
            topic = "topic",
            objectMapper = objectMapper
        )

        beforeEach { clearAllMocks() }

        scenario("successful payment created send") {
            val operation = aCreatedAnnulment()
            val serializedOperation = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(operation) } returns serializedOperation

            // expect that
            eventProducer.produce(operation)

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(operation) }
            verify(exactly = 1) { kafkaTemplate.send("topic", "0f14d0ab-9605-4a62-a9e4-5ed26688389b", serializedOperation) }
        }

        scenario("payment created created error") {
            val operation = aCreatedAnnulment()
            val serializedOperation = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(operation) } returns serializedOperation
            every { kafkaTemplate.send("topic", "0f14d0ab-9605-4a62-a9e4-5ed26688389b", serializedOperation) } throws Exception()

            // expect that
            eventProducer.produce(operation) shouldNotBeSameInstanceAs QueueProducerNotWritten().left()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(operation) }
            verify(exactly = 1) { kafkaTemplate.send("topic", "0f14d0ab-9605-4a62-a9e4-5ed26688389b", serializedOperation) }
        }
    }
})
