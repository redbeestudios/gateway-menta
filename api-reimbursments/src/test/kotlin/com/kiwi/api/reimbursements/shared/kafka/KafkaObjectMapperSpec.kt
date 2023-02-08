package com.kiwi.api.reimbursements.shared.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class KafkaObjectMapperSpec : FeatureSpec({

    val objectMapper = mockk<ObjectMapper>()
    val kafkaObjectMapper = KafkaObjectMapper(objectMapper)

    feature("serialize") {

        scenario("success") {
            val value = aCreatedAnnulment()
            val serializedValue = "a serialized value"

            every { objectMapper.writeValueAsString(value) } returns serializedValue

            kafkaObjectMapper.serialize(value) shouldBe serializedValue
        }
    }

    feature("deserialize") {

        scenario("success") {
            val value = aCreatedAnnulment()
            val serializedValue = "a serialized value"

            every { objectMapper.readValue(serializedValue, any<TypeReference<*>>()) } returns value

            kafkaObjectMapper.deserialize<CreatedAnnulment>(serializedValue) shouldBe value
        }
    }
})
