package com.menta.api.users.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.users.application.port.`in`.FindUserPortIn
import com.menta.api.users.application.port.out.FindUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userDisabledError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindUserUseCase(
    private val findUserPortOut: FindUserPortOut
) : FindUserPortIn {

    override fun findBy(email: Email, type: UserType): Either<ApplicationError, User> =
        doFindBy(email, type).flatMap {
            it.shouldBeEnabled()
        }

    private fun doFindBy(email: Email, type: UserType) =
        findUserPortOut.findBy(email, type)
            .logRight { info("user found: {}", it) }

    private fun User.shouldBeEnabled() =
        if (enabled) {
            right()
        } else {
            userDisabledError(attributes.email).left()
        }.logEither(
            { error("user disabled: {}", it) },
            { info("user enabled: {}", it) }
        )

    companion object : CompanionLogger()
}
