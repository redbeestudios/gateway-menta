package com.menta.api.users.authorities.adapter.out.event

import arrow.core.left
import arrow.core.right
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.shared.config.kafka.KafkaObjectMapper
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.queueProducerNotWritten
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture

class AssignUserAuthorityProducerSpec : FeatureSpec({

    val topic = "a topic"
    val objectMapper: KafkaObjectMapper = mockk()
    val kafkaTemplate: KafkaTemplate<String, String> = mockk()
    val future: ListenableFuture<SendResult<String, String>?> = mockk()

    beforeEach { clearAllMocks() }

    val producer = AssignUserAuthorityProducer(
        kafkaTemplate,
        topic,
        objectMapper
    )

    feature("produce event") {
        scenario("successful producer") {
            val userAssignAuthority = anUserAssignAuthority

            // given mocked dependencies
            every { objectMapper.serialize(userAssignAuthority) } returns ""
            every { kafkaTemplate.send(topic, any()) } returns future

            // expect that
            producer.produce(userAssignAuthority) shouldBe Unit.right()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(userAssignAuthority) }
            verify(exactly = 1) { kafkaTemplate.send(topic, any()) }
        }
        scenario("error producer") {
            val userAssignAuthority = anUserAssignAuthority

            // given mocked dependencies
            every { objectMapper.serialize(userAssignAuthority) } returns ""
            every { kafkaTemplate.send(topic, any()) } throws RuntimeException("an exception")

            // expect that
            producer.produce(userAssignAuthority) shouldBe queueProducerNotWritten().left()

            // dependencies called
            verify(exactly = 1) { objectMapper.serialize(userAssignAuthority) }
            verify(exactly = 1) { kafkaTemplate.send(topic, any()) }
        }
    }
})
