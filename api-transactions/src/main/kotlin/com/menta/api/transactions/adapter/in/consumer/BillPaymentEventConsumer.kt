package com.menta.api.transactions.adapter.`in`.consumer

import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedBillPayment
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.shared.kafka.ConsumerMessageResolver
import com.menta.api.transactions.shared.kafka.KafkaMessageConsumer
import com.menta.api.transactions.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class BillPaymentEventConsumer(
    private val createTransaction: CreateTransactionInPort,
    private val toDomainMapper: ToTransactionMapper,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.bill-payment.created}"],
        groupId = "\${event.group.created}"
    )
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedBillPayment>(message, topic)
            .consume(ack) {
                it.message.let {
                    it.toDomain()
                        .save()
                        .also {
                            ack.acknowledge()
                                .log { info("Bill Payment event processed") }
                        }
                }
            }

    private fun CreatedBillPayment.toDomain() =
        toDomainMapper.map(this, OperationType.PAYMENT)
            .log { info("transaction builded: {}") }

    private fun Transaction.save() =
        createTransaction.execute(this)
            .log { info("transaction saved: {}", it) }

    companion object : CompanionLogger()
}
