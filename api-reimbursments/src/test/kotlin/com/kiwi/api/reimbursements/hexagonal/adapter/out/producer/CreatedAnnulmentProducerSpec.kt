package com.kiwi.api.reimbursements.hexagonal.adapter.out.producer

import arrow.core.left
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError.Companion.queueProducerNotWritten
import com.kiwi.api.reimbursements.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class CreatedAnnulmentProducerSpec : FeatureSpec({
    feature("Produce created payment event ") {
        lateinit var eventProducer: CreatedAnnulmentsProducer

        val kafkaTemplate: KafkaTemplate<String, String> = mockk(relaxed = true)
        val objectMapper = mockk<KafkaObjectMapper>()

        eventProducer = CreatedAnnulmentsProducer(
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
            eventProducer.save(operation)

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(operation) }
            verify(exactly = 1) {
                kafkaTemplate.send("topic", operation.id.toString(), serializedOperation)
            }
        }

        scenario("payment created error") {
            val operation = aCreatedAnnulment()
            val serializedOperation = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(operation) } returns serializedOperation
            every { kafkaTemplate.send("topic", operation.id.toString(), serializedOperation) } throws Exception()

            // expect that
            eventProducer.save(operation) shouldNotBeSameInstanceAs queueProducerNotWritten().left()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(operation) }
            verify(exactly = 1) { kafkaTemplate.send("topic", operation.id.toString(), serializedOperation) }
        }
    }
})
