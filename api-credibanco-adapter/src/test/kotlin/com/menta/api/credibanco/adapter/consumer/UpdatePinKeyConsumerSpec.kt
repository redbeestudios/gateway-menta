package com.menta.api.credibanco.adapter.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.credibanco.aSecret
import com.menta.api.credibanco.application.port.`in`.ClearUpdatedTerminalsPortIn
import com.menta.api.credibanco.shared.kafka.ConsumerMessageResolver
import com.menta.api.credibanco.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class UpdatePinKeyConsumerSpec : FeatureSpec({

    val portIn = mockk<ClearUpdatedTerminalsPortIn>()
    val objectMapper = mockk<ObjectMapper>()
    val messageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val useCase = UpdatePinKeyConsumer(
        clearUpdatedTerminalsPortIn = portIn,
        consumerMessageResolver = messageResolver
    )

    val ack: Acknowledgment = mockk(relaxed = true)

    beforeEach {
        clearAllMocks()
    }

    feature("consume") {

        scenario("successful clearing of updated terminals") {

            every { portIn.execute(aSecret.acquirer.name) } returns true.right()
            every { objectMapper.readValue("a secrets string", any<TypeReference<*>>()) } returns aSecret

            useCase.consume("a secrets string", "a topic", ack)

            verify(exactly = 1) { portIn.execute(aSecret.acquirer.name) }
            verify(exactly = 1) { objectMapper.readValue("a secrets string", any<TypeReference<*>>())}
        }
    }
})
