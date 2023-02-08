package com.menta.apisecrets.adapter.out.event

import com.menta.apisecrets.aProducerRecord
import com.menta.apisecrets.aSecrets
import com.menta.apisecrets.aSerializedMessage
import com.menta.apisecrets.aTopic
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.kafka.KafkaObjectMapper
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate

class TerminalUpdateProducerSpec : FeatureSpec({

    feature("produce") {

        val template = mockk<KafkaTemplate<String, String>>(relaxed = true)
        val mapper = mockk<KafkaObjectMapper>()

        val producer = TerminalUpdateProducer(kafkaTemplate = template, topic = aTopic, objectMapper = mapper)

        beforeEach { clearAllMocks() }

        scenario("even sent successfully") {

            every { mapper.serialize(aSecrets.context.terminal) } returns aSerializedMessage

            producer.produce(aSecrets)

            verify(exactly = 1) { mapper.serialize(aSecrets.context.terminal) }
            verify(exactly = 1) { template.send(aProducerRecord) }
        }

        scenario("event not sent") {

            every { mapper.serialize(aSecrets.context.terminal) } returns aSerializedMessage
            every { template.send(aProducerRecord) } throws Exception()

            producer.produce(aSecrets) shouldBeLeft ApplicationError(code = "400", message = "Could not insert the message in the created payment queue", status = HttpStatus.INTERNAL_SERVER_ERROR)

            verify(exactly = 1) { mapper.serialize(aSecrets.context.terminal) }
            verify(exactly = 1) { template.send(aProducerRecord) }
        }
    }
})
