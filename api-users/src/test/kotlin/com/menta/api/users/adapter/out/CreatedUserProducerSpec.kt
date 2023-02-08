package com.menta.api.users.adapter.out

import arrow.core.left
import com.menta.api.users.aUser
import com.menta.api.users.shared.kafka.KafkaObjectMapper
import com.menta.api.users.shared.other.error.model.ApplicationError
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class CreatedUserProducerSpec : FeatureSpec({
    feature("Produce created user event ") {
        lateinit var eventProducer: CreatedUserProducer

        val kafkaTemplate: KafkaTemplate<String, String> = mockk(relaxed = true)
        val objectMapper = mockk<KafkaObjectMapper>()

        eventProducer = CreatedUserProducer(
            kafkaTemplate = kafkaTemplate,
            topic = "topic",
            objectMapper = objectMapper
        )

        beforeEach { clearAllMocks() }

        scenario("successful user created send") {
            val user = aUser()
            val serializedUser = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(user) } returns serializedUser

            // expect that
            eventProducer.produce(user)

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(user) }
            verify(exactly = 1) { kafkaTemplate.send("topic", serializedUser) }
        }

        scenario("user created return ApplicationError") {
            val user = aUser()
            val serializedUser = "a serialized operation"

            // given mocked dependencies
            every { objectMapper.serialize(user) } returns serializedUser
            every { kafkaTemplate.send("topic", serializedUser) } throws Exception()

            // expect that
            eventProducer.produce(user) shouldNotBeSameInstanceAs ApplicationError.left()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(user) }
            verify(exactly = 1) { kafkaTemplate.send("topic", serializedUser) }
        }
    }
})
