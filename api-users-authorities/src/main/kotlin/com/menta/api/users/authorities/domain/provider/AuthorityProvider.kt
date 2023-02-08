package com.menta.api.users.authorities.domain.provider

import arrow.core.Either
import arrow.core.leftIfNull
import arrow.core.right
import com.menta.api.users.authorities.application.port.out.FindUserAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.shared.other.authorities.UserAuthoritiesProperties
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AuthorityProvider(
    private val properties: UserAuthoritiesProperties
) : FindUserAuthorityPortOut {

    override fun provideBy(type: UserType): Either<ApplicationError, UserAuthority> =
        properties.provider.authorities
            .firstOrNull { it.type == type }
            .right()
            .leftIfNull { missingConfigurationForUserType(type) }
            .logEither(
                { warn("Missing user authority config for user type: {}", it) },
                { info("User authority for user type {} : {}", type, it) }
            )

    companion object : CompanionLogger()
}
