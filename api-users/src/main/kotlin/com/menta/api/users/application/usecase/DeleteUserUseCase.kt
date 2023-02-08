package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.DeleteUserPortIn
import com.menta.api.users.application.port.out.DeleteUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteUserUseCase(
    private val deleteUserPortOut: DeleteUserPortOut
) : DeleteUserPortIn {

    override fun deleteBy(email: Email, type: UserType): Either<ApplicationError, Unit> =
        doDeleteBy(email, type)

    private fun doDeleteBy(email: Email, type: UserType) =
        deleteUserPortOut.deleteBy(email, type)
            .logRight { info("user delete: {}", it) }

    companion object : CompanionLogger()
}
