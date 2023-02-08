package com.menta.api.credibanco.adapter.consumer

import com.menta.api.credibanco.application.port.`in`.RegisterUpdatedTerminalPortIn
import com.menta.api.credibanco.domain.SecretsTerminal
import com.menta.api.credibanco.shared.kafka.ConsumerMessageResolver
import com.menta.api.credibanco.shared.kafka.KafkaMessageConsumer
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class UpdateTerminalConsumer(
    private val registerUpdatedTerminalPortIn: RegisterUpdatedTerminalPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.terminal.secrets.updated}"],
        groupId = "\${event.group.terminal.secrets.updated}"
    )
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) {
        generateConsumerMessage<SecretsTerminal>(message, topic)
            .consume(ack) {
                registerUpdatedTerminalPortIn.execute("${it.message.id}")
            }
    }

    companion object : CompanionLogger()
}
