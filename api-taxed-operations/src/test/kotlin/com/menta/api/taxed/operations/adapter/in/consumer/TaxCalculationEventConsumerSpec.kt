package com.menta.api.taxed.operations.adapter.`in`.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.api.taxed.operations.adapter.`in`.consumer.mapper.ToPaymentTaxedOperationMapper
import com.menta.api.taxed.operations.application.aPaymentTaxCalculation
import com.menta.api.taxed.operations.application.aPaymentTaxedOperation
import com.menta.api.taxed.operations.application.aTaxedOperation
import com.menta.api.taxed.operations.applications.port.`in`.CreateTaxedOperationPortIn
import com.menta.api.taxed.operations.shared.kafka.ConsumerMessageResolver
import com.menta.api.taxed.operations.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.support.Acknowledgment

class TaxCalculationEventConsumerSpec : FeatureSpec({
    val createTaxOperation: CreateTaxedOperationPortIn = mockk()
    val ack: Acknowledgment = mockk(relaxed = true)
    val objectMapper = mockk<ObjectMapper>()
    val consumerMessageResolver = ConsumerMessageResolver(KafkaObjectMapper(objectMapper))
    val toDomainMapper: ToPaymentTaxedOperationMapper = mockk()

    val consumer = TaxCalculationEventConsumer(createTaxOperation, toDomainMapper, consumerMessageResolver)

    beforeEach { clearAllMocks() }

    feature("consume event") {

        scenario("successful consume") {
            val serializedMessage = "a message"
            val message = aPaymentTaxCalculation()
            val topic = "a topic"
            val operation = aPaymentTaxedOperation()
            val taxedOperation = aTaxedOperation()
            // given mocked dependencies
            every { toDomainMapper.map(message) } returns operation
            every { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) } returns message
            every { createTaxOperation.execute(operation) } returns taxedOperation

            // expect that
            consumer.consume(serializedMessage, topic, ack) shouldBe Unit

            // dependencies called
            verify(exactly = 1) { createTaxOperation.execute(operation) }
            verify(exactly = 1) { objectMapper.readValue(serializedMessage, any<TypeReference<*>>()) }
            verify(exactly = 1) { ack.acknowledge() }
        }
    }
})
