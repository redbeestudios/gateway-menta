package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.CreateUserPortIn
import com.menta.api.users.application.port.out.CreateUserPortOut
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateUserUseCase(
    private val createUserPortOut: CreateUserPortOut
) : CreateUserPortIn {

    override fun create(newUser: NewUser): Either<ApplicationError, User> =
        createUserPortOut.create(newUser)
            .logRight { info("user created: {}", it) }

    companion object : CompanionLogger()
}
