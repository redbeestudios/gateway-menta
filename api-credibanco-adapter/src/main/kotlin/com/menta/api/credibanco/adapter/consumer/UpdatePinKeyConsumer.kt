package com.menta.api.credibanco.adapter.consumer

import com.menta.api.credibanco.application.port.`in`.ClearUpdatedTerminalsPortIn
import com.menta.api.credibanco.domain.Secret
import com.menta.api.credibanco.shared.kafka.ConsumerMessageResolver
import com.menta.api.credibanco.shared.kafka.KafkaMessageConsumer
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class UpdatePinKeyConsumer(
    private val clearUpdatedTerminalsPortIn: ClearUpdatedTerminalsPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.pin.key.updated}"],
        groupId = "\${event.group.pin.key.updated}"
    )
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) {
        generateConsumerMessage<Secret>(message, topic)
            .consume(ack) {
                clearUpdatedTerminalsPortIn.execute("${it.message.acquirer}")
            }
    }
}
