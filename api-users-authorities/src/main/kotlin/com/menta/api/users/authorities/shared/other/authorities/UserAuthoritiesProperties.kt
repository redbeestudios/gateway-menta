package com.menta.api.users.authorities.shared.other.authorities

import com.menta.api.users.authorities.domain.UserAuthority
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "config")
data class UserAuthoritiesProperties(
    val provider: Provider
) {
    data class Provider(
        val authorities: List<UserAuthority>
    )
}
