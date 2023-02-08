package com.kiwi.api.payments.shared.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.payments.hexagonal.application.aCreatedPayment
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class KafkaObjectMapperSpec : FeatureSpec({

    val objectMapper = mockk<ObjectMapper>()
    val kafkaObjectMapper = KafkaObjectMapper(objectMapper)

    feature("serialize") {

        scenario("success") {
            val value = aCreatedPayment()
            val serializedValue = "a serialized value"

            every { objectMapper.writeValueAsString(value) } returns serializedValue

            kafkaObjectMapper.serialize(value) shouldBe serializedValue
        }
    }

    feature("deserialize") {

        scenario("success") {
            val value = aCreatedPayment()
            val serializedValue = "a serialized value"

            every { objectMapper.readValue(serializedValue, any<TypeReference<*>>()) } returns value

            kafkaObjectMapper.deserialize<CreatedPayment>(serializedValue) shouldBe value
        }
    }
})
