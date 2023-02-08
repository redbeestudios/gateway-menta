package com.menta.api.users.authorities.application.usecase

import arrow.core.Either
import com.menta.api.users.authorities.application.port.`in`.FindUserAuthorityPortIn
import com.menta.api.users.authorities.application.port.out.FindUserAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindAuthorityUseCase(
    private val provider: FindUserAuthorityPortOut
) : FindUserAuthorityPortIn {

    override fun execute(type: UserType): Either<ApplicationError, UserAuthority> =
        provider.provideBy(type)
            .log { info("user authorities found : {}", it) }

    companion object : CompanionLogger()
}
