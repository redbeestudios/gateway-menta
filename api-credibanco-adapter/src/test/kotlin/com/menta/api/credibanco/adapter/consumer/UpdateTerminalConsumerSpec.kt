package com.menta.api.credibanco.adapter.consumer

import arrow.core.right
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.credibanco.aSecretsTerminal
import com.menta.api.credibanco.aTerminalId
import com.menta.api.credibanco.application.usecase.RegisterUpdatedTerminalUseCase
import com.menta.api.credibanco.shared.kafka.ConsumerMessageResolver
import com.menta.api.credibanco.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class UpdateTerminalConsumerSpec : FeatureSpec({

    val portIn = mockk<RegisterUpdatedTerminalUseCase>()
    val objectMapper = mockk<ObjectMapper>()
    val messageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))

    val useCase = UpdateTerminalConsumer(
        registerUpdatedTerminalPortIn = portIn,
        consumerMessageResolver = messageResolver
    )

    val ack: Acknowledgment = mockk(relaxed = true)

    beforeEach {
        clearAllMocks()
    }

    feature("consume") {

        scenario("successful terminal update") {

            every { portIn.execute(aTerminalId) } returns aTerminalId.right()
            every { objectMapper.readValue("a terminal string", any<TypeReference<*>>()) } returns aSecretsTerminal

            useCase.consume("a terminal string", "a topic", ack)

            verify(exactly = 1) { portIn.execute(aTerminalId) }
            verify(exactly = 1) { objectMapper.readValue("a terminal string", any<TypeReference<*>>()) }
        }
    }
})
