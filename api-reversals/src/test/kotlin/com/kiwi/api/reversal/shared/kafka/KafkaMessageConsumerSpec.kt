package com.kiwi.api.reversal.shared.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reversal.hexagonal.application.aConsumerMessage
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.domain.operations.CreatedPayment
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
            val deserializedMessage = aCreatedPayment()
            val topic = "a topic"
            val key = "a key"
            val partition = 1L

            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns deserializedMessage
            kafkaMessageConsumer.generateConsumerMessage<CreatedPayment>(
                message = message,
                topic = topic,
                key = key,
                partitionId = partition
            )

            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
        }

        scenario("consumer message with required fields") {
            val message = "a message"
            val deserializedMessage = aCreatedPayment()
            val topic = "a topic"

            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns deserializedMessage
            kafkaMessageConsumer.generateConsumerMessage<CreatedPayment>(
                message = message,
                topic = topic,
                key = null,
                partitionId = null
            )

            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
        }
    }

    feature("consume message") {

        scenario("message consumed") {
            val block = mockk<(consumerMessage: ConsumerMessage<CreatedPayment>) -> Unit>(relaxed = true)
            val consumerMessage = aConsumerMessage(aCreatedPayment())

            with(kafkaMessageConsumer) {
                consumerMessage.consume(ack, block) shouldBe Unit
            }

            verify(exactly = 1) { ack.acknowledge() }
            verify(exactly = 1) { block.invoke(consumerMessage) }
        }
    }
})
