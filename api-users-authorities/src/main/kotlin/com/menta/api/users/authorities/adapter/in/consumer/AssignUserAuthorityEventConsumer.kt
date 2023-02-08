package com.menta.api.users.authorities.adapter.`in`.consumer

import com.menta.api.users.authorities.application.port.`in`.AssignAuthorityPortIn
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.config.kafka.ConsumerMessageResolver
import com.menta.api.users.authorities.shared.config.kafka.KafkaMessageConsumer
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class AssignUserAuthorityEventConsumer(
    private val assignAuthorityPortIn: AssignAuthorityPortIn,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.authority.created}"],
        groupId = "\${event.group.user-authority.assign}"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".user.assign.authority-retry", dltTopicSuffix = ".user.assign.authority-dlt"
    )
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<UserAssignAuthority>(message, topic)
            .consume(ack) {
                it.message
                    .doAssignUserAuthority()
                    .logEither(
                        { error("Error assign authority event processed : {}", it) },
                        { info("User assign authority event processed") }
                    )
            }

    private fun UserAssignAuthority.doAssignUserAuthority() =
        assignAuthorityPortIn.assign(this)

    companion object : CompanionLogger()
}
