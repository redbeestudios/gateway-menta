package com.kiwi.api.payments.hexagonal.adapter.port.out.event

import com.kiwi.api.payments.hexagonal.adapter.out.event.ReversalPaymentProducer
import com.kiwi.api.payments.hexagonal.application.aReversalOperation
import com.kiwi.api.payments.hexagonal.application.port.out.ReverseOperationRepositoryPortOut
import com.kiwi.api.payments.shared.kafka.KafkaObjectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class ReversalPaymentProducerSpec : FeatureSpec({

    feature("Produce reversal payment event") {
        lateinit var eventProducer: ReverseOperationRepositoryPortOut

        val kafkaTemplate: KafkaTemplate<String, String> = mockk(relaxed = true)
        val objectMapper = mockk<KafkaObjectMapper>()

        eventProducer = ReversalPaymentProducer(
            kafkaTemplate = kafkaTemplate,
            topic = "topic",
            objectMapper = objectMapper
        )

        scenario("Reversal Payment happy path") {
            val reversalOperation = aReversalOperation()
            val message = "Serialization of Reversal Operation"

            every { objectMapper.serialize(reversalOperation) } returns message

            eventProducer.produce(reversalOperation)

            verify(exactly = 1) { objectMapper.serialize(reversalOperation) }
        }
    }
})
