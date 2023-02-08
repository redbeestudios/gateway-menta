package com.menta.api.users.authorities.adapter.`in`.consumer

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.users.authorities.adapter.`in`.consumer.model.User
import com.menta.api.users.authorities.application.mapper.ToUserAssignAuthorityMapper
import com.menta.api.users.authorities.application.port.`in`.AssignAuthoritiesPortIn
import com.menta.api.users.authorities.application.port.`in`.FindUserAuthorityPortIn
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.shared.config.kafka.ConsumerMessageResolver
import com.menta.api.users.authorities.shared.config.kafka.KafkaMessageConsumer
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
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
class CreatedUserEventConsumer(
    private val findUserAuthorityPortIn: FindUserAuthorityPortIn,
    private val assignAuthorities: AssignAuthoritiesPortIn,
    private val toUserAssignAuthorityMapper: ToUserAssignAuthorityMapper,
    consumerMessageResolver: ConsumerMessageResolver
) : KafkaMessageConsumer(consumerMessageResolver) {

    @KafkaListener(
        topics = ["\${event.topic.user.created}"],
        groupId = "\${event.group.user-authority.distribute}"
    )
    @RetryableTopic(
        attempts = "10", backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".user.distribute.authority-retry", dltTopicSuffix = ".user.distribute.authority-dlt"
    )
    fun consume(@Payload message: String, @Header(RECEIVED_TOPIC) topic: String, ack: Acknowledgment) =
        generateConsumerMessage<User>(message, topic)
            .consume(ack) { user ->
                user.message
                    .findAuthoritiesByType()
                    .flatMap { it.assignAuthorities() }
                    .log {
                        error("Error user created event processed : {}", it)
                        info("User user created event processed")
                    }
            }

    private fun User.findAuthoritiesByType() =
        findUserAuthorityPortIn.execute(attributes.type)
            .map { userAuthority ->
                userAuthority.authorities.map { authority ->
                    toUserAssignAuthorityMapper.mapFrom(this, authority)
                }
            }

    private fun List<UserAssignAuthority>.assignAuthorities() =
        assignAuthorities.execute(this)

    companion object : CompanionLogger()
}
