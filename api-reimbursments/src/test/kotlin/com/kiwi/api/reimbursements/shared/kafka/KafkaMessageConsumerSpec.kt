package com.kiwi.api.reimbursements.shared.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reimbursements.hexagonal.application.aConsumerMessage
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class KafkaMessageConsumerSpec : FeatureSpec({

    val ack = mockk<Acknowledgment>(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val kafkaObjectMapper = KafkaObjectMapper(objectMapper)
    val consumerMessageResolver = ConsumerMessageResolver(kafkaObjectMapper)
    val kafkaMessageConsumer = object : KafkaMessageConsumer(consumerMessageResolver) {}

    beforeEach { clearAllMocks() }

    feature("generate consumer message") {

        scenario("consumer message with all fields") {
            val message = "a message"
            val deserializedMessage = aCreatedAnnulment()
            val topic = "a topic"
            val key = "a key"
            val partition = 1L

            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns deserializedMessage
            kafkaMessageConsumer.generateConsumerMessage<CreatedAnnulment>(
                message = message,
                topic = topic,
                key = key,
                partitionId = partition
            )

            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
        }

        scenario("consumer message with required fields") {
            val message = "a message"
            val deserializedMessage = aCreatedAnnulment()
            val topic = "a topic"

            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns deserializedMessage
            kafkaMessageConsumer.generateConsumerMessage<CreatedAnnulment>(
                message = message,
                topic = topic,
            )

            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
        }
    }

    feature("consume message") {

        scenario("message consumed") {
            val block = mockk<(consumerMessage: ConsumerMessage<CreatedAnnulment>) -> Unit>(relaxed = true)
            val consumerMessage = aConsumerMessage(aCreatedAnnulment())

            with(kafkaMessageConsumer) {
                consumerMessage.consume(ack, block) shouldBe Unit
            }

            verify(exactly = 1) { ack.acknowledge() }
            verify(exactly = 1) { block.invoke(consumerMessage) }
        }
    }
})
