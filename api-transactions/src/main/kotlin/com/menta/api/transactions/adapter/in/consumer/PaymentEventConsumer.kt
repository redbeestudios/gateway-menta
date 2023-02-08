package com.menta.api.transactions.adapter.`in`.consumer

import com.menta.api.transactions.adapter.`in`.consumer.mapper.ToTransactionMapper
import com.menta.api.transactions.adapter.`in`.consumer.model.CreatedPayment
import com.menta.api.transactions.application.port.`in`.CreateTransactionInPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode.FAILED
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
class PaymentEventConsumer(
    private val createTransaction: CreateTransactionInPort,
    private val toDomainMapper: ToTransactionMapper,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.payment.created}"],
        groupId = "\${event.group.created}"
    )
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<CreatedPayment>(message, topic)
            .consume(ack) {
                it.message.let {
                    if (it.isFailed()) {
                        null
                            .log { error("Payment Failed is not save") }
                    } else {
                        it.toDomain()
                            .save()
                            .also {
                                ack.acknowledge()
                                    .log { info("Payment event processed") }
                            }
                    }
                }
            }

    private fun CreatedPayment.isFailed() =
        authorization.status.code == FAILED

    private fun CreatedPayment.toDomain() =
        toDomainMapper.map(this, OperationType.PAYMENT)
            .log { info("transaction builded: {}") }

    private fun Transaction.save() =
        createTransaction.execute(this)
            .log { info("transaction saved: {}", it) }

    companion object : CompanionLogger()
}
