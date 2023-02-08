package com.menta.api.users.authorities.adapter.`in`.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.users.authorities.anUser
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.anUserAuthority
import com.menta.api.users.authorities.application.mapper.ToUserAssignAuthorityMapper
import com.menta.api.users.authorities.application.port.`in`.AssignAuthoritiesPortIn
import com.menta.api.users.authorities.application.port.`in`.FindUserAuthorityPortIn
import com.menta.api.users.authorities.authority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.shared.config.kafka.ConsumerMessageResolver
import com.menta.api.users.authorities.shared.config.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class CreatedUserEventConsumerSpec : FeatureSpec({

    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val findUserAuthorityPortIn: FindUserAuthorityPortIn = mockk()
    val toUserAssignAuthorityMapper: ToUserAssignAuthorityMapper = mockk()
    val assignAuthoritiesPortIn: AssignAuthoritiesPortIn = mockk()

    val consumer = CreatedUserEventConsumer(
        findUserAuthorityPortIn,
        assignAuthoritiesPortIn,
        toUserAssignAuthorityMapper,
        consumerMessageResolver
    )

    feature("consume event create user") {
        val message = "a message"
        val topic = "a topic"
        val userAuthority = anUserAuthority
        val userAssignAuthority = anUserAssignAuthority
        val authority = authority
        val user = anUser

        scenario("successful consume") {
            // given mocked dependencies
            every { objectMapper.readValue(message, any<TypeReference<*>>()) } returns user
            every { findUserAuthorityPortIn.execute(UserType.MERCHANT) } returns userAuthority.right()
            every { toUserAssignAuthorityMapper.mapFrom(user, authority) } returns userAssignAuthority
            every { assignAuthoritiesPortIn.execute(listOf(userAssignAuthority)) } returns Unit.right()

            // expect that
            consumer.consume(message, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { objectMapper.readValue(message, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
            verify(exactly = 1) { findUserAuthorityPortIn.execute(UserType.MERCHANT) }
            verify(exactly = 1) { toUserAssignAuthorityMapper.mapFrom(user, authority) }
            verify(exactly = 1) { assignAuthoritiesPortIn.execute(listOf(userAssignAuthority)) }
        }
    }
})
