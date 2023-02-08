package com.kiwi.api.reimbursements.shared.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ConsumerMessageResolverSpec : FeatureSpec({

    val objectMapper = mockk<ObjectMapper>()
    val kafkaObjectMapper = KafkaObjectMapper(objectMapper)
    val resolver = ConsumerMessageResolver(kafkaObjectMapper)

    feature("consume Message") {

        scenario("message consumed") {
            val serializedMessage = "a message"
            val consumerMessage = ConsumerMessage(
                message = aCreatedAnnulment(),
                key = "a key",
                partitionId = 1L,
                topic = "a topic"
            )

            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns consumerMessage.message

            resolver.resolve<CreatedAnnulment>(
                serializedMessage,
                consumerMessage.key,
                consumerMessage.partitionId,
                consumerMessage.topic
            ) shouldBe consumerMessage
        }

        scenario("message consumed with required fields") {
            val serializedMessage = "a message"
            val consumerMessage = ConsumerMessage(
                message = aCreatedAnnulment(),
                topic = "a topic",
                key = null,
                partitionId = null
            )

            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns consumerMessage.message

            resolver.resolve<CreatedAnnulment>(
                serializedMessage,
                consumerMessage.key,
                consumerMessage.partitionId,
                consumerMessage.topic
            ) shouldBe consumerMessage
        }
    }
})
