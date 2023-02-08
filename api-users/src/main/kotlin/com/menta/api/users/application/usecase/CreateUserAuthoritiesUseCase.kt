package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.CreateUserAuthoritiesPortIn
import com.menta.api.users.application.port.out.CreatedUserEventPortOut
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateUserAuthoritiesUseCase(
    private val createUserPortOut: CreatedUserEventPortOut
): CreateUserAuthoritiesPortIn {

    override fun execute(createdUser: User): Either<ApplicationError, Unit> =
        createUserPortOut.produce(createdUser)
            .logEither(
                { error("Error with created.user queue") },
                { info("Sent user into queue created.user") }
            )

    companion object: CompanionLogger()
}
