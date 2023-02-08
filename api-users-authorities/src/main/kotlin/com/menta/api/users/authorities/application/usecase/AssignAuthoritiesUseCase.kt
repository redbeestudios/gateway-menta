package com.menta.api.users.authorities.application.usecase

import arrow.core.Either
import arrow.core.right
import com.menta.api.users.authorities.application.port.`in`.AssignAuthoritiesPortIn
import com.menta.api.users.authorities.application.port.out.AssignUserAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AssignAuthoritiesUseCase(
    private val assignUserAuthorityPortOut: AssignUserAuthorityPortOut,
) : AssignAuthoritiesPortIn {

    override fun execute(userAssignAuthorities: List<UserAssignAuthority>): Either<ApplicationError, Unit> =
        userAssignAuthorities
            .assignAuthorities()
            .right()

    private fun List<UserAssignAuthority>.assignAuthorities() =
        this.forEach { it.produceMessage() }

    private fun UserAssignAuthority.produceMessage(): Either<ApplicationError, Unit> =
        assignUserAuthorityPortOut.produce(this)
            .logEither(
                { error("Error with send event into queue : {}", it) },
                { info("Sent authority user into queue") }
            )

    companion object : CompanionLogger()
}
