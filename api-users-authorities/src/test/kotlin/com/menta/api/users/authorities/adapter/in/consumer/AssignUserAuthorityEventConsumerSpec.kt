package com.menta.api.users.authorities.adapter.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.application.port.`in`.AssignAuthorityPortIn
import com.menta.api.users.authorities.shared.config.kafka.ConsumerMessageResolver
import com.menta.api.users.authorities.shared.config.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class AssignUserAuthorityEventConsumerSpec : FeatureSpec({

    val assignAuthorityPortIn: AssignAuthorityPortIn = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val consumer = AssignUserAuthorityEventConsumer(
        assignAuthorityPortIn,
        consumerMessageResolver
    )

    feature("consume event assign user authority") {
        scenario("successful consume") {
            val message = "a message"
            val topic = "a topic"
            val userAssignAuthority = anUserAssignAuthority

            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns userAssignAuthority
            every { assignAuthorityPortIn.assign(userAssignAuthority) } returns Unit.right()

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
            verify(exactly = 1) { assignAuthorityPortIn.assign(userAssignAuthority) }
        }
    }
})
