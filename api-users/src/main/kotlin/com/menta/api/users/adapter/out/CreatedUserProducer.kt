package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.users.application.port.out.CreatedUserEventPortOut
import com.menta.api.users.domain.User
import com.menta.api.users.shared.kafka.KafkaObjectMapper
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.queueProducerNotWritten
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CreatedUserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${event.topic.user.created}")
    private val topic: String,
    private val objectMapper: KafkaObjectMapper
): CreatedUserEventPortOut {

    override fun produce(createdUser: User): Either<ApplicationError, Unit> =
        try {
            kafkaTemplate.send(topic, createdUser.asMessage())
                .log { info("create user event produced: {}", createdUser) }
                .let { Unit.right() }
        } catch (e: Exception) {
            queueProducerNotWritten("created.user").left()
                .log { error("Error with produce create user event: {}", createdUser) }
        }

    private fun User.asMessage() =
        objectMapper.serialize(this)

    companion object : CompanionLogger()
}
