package com.menta.api.users.authorities.adapter.out.event

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.users.authorities.application.port.out.AssignUserAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.config.kafka.KafkaObjectMapper
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.queueProducerNotWritten
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class AssignUserAuthorityProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.authority.created}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
) : AssignUserAuthorityPortOut {

    override fun produce(message: UserAssignAuthority): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, message.asMessage())
                .log { info("Assign user authority event produced: {}", message) }
                .let { Unit.right() }
        } catch (e: Exception) {
            queueProducerNotWritten().left()
                .log { error("Error with produce UserAssignAuthority event: {}", message) }
        }

    private fun UserAssignAuthority.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
