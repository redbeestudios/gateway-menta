package com.menta.apisecrets.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.apisecrets.application.port.out.TerminalUpdateProducerOutPort
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.queueMessageNotWritten
import com.menta.apisecrets.shared.kafka.KafkaObjectMapper
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class TerminalUpdateProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.terminal.secrets.updated}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : TerminalUpdateProducerOutPort {

    override fun produce(message: Secrets): Either<ApplicationError, Unit> =
        try {
            with(message.context) {
                ProducerRecord(
                    topic,
                    null,
                    "${terminal.id}",
                    terminal.asMessage(),
                    headers(acquirer)
                )
            }
                .send()
                .log { info("terminal update event produced: {}", message) }
            Unit.right()
        } catch (e: Exception) {
            queueMessageNotWritten().left()
                .log { error("error producing terminal update event: {}", message) }
        }

    private fun ProducerRecord<String, String>.send() =
        kafkaTemplate.send(this)

    private fun Terminal.asMessage() =
        objectMapper.serialize(this)

    private fun headers(acquirer: Acquirer) =
        listOf(
            Header(HEADER, acquirer.name)
        )

    companion object : CompanionLogger() {
        const val HEADER = "ACQUIRER"
    }
}

data class Header(val key: String, val value: String) : Header {
    override fun key() = key
    override fun value() = value.toByteArray()

}
