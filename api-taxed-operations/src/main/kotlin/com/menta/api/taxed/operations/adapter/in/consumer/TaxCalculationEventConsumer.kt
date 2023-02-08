package com.menta.api.taxed.operations.adapter.`in`.consumer

import com.menta.api.taxed.operations.adapter.`in`.consumer.mapper.ToPaymentTaxedOperationMapper
import com.menta.api.taxed.operations.applications.port.`in`.CreateTaxedOperationPortIn
import com.menta.api.taxed.operations.domain.PaymentTaxCalculation
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import com.menta.api.taxed.operations.shared.kafka.ConsumerMessageResolver
import com.menta.api.taxed.operations.shared.kafka.KafkaMessageConsumer
import com.menta.api.taxed.operations.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class TaxCalculationEventConsumer(
    private val createTaxedOperation: CreateTaxedOperationPortIn,
    private val toDomainMapper: ToPaymentTaxedOperationMapper,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {
    @KafkaListener(
        topics = ["\${event.topic.taxed.operation}"],
        groupId = "\${event.group.persist.taxed.operation}"
    )
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<PaymentTaxCalculation>(message, topic)
            .consume(ack) {
                it.message
                    .toDomain()
                    .save()
                    .log { info("Taxed operation event processed") }
            }

    private fun PaymentTaxedOperation.save() =
        createTaxedOperation.execute(this)

    private fun PaymentTaxCalculation.toDomain() =
        toDomainMapper.map(this)

    companion object : CompanionLogger()
}
