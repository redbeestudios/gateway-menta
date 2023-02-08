package com.menta.api.users.authorities.application.usecase

import arrow.core.Either
import com.menta.api.users.authorities.application.port.`in`.AssignAuthorityPortIn
import com.menta.api.users.authorities.application.port.out.AssignAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AssignAuthorityUseCase(
    private val assignAuthorityPortOut: AssignAuthorityPortOut,
) : AssignAuthorityPortIn {

    override fun assign(userAssignAuthority: UserAssignAuthority): Either<ApplicationError, Unit> =
        userAssignAuthority
            .doAssignUserAuthority()

    private fun UserAssignAuthority.doAssignUserAuthority() =
        assignAuthorityPortOut.assign(this)
            .log { info("user authority assign : {}", it) }

    companion object : CompanionLogger()
}
