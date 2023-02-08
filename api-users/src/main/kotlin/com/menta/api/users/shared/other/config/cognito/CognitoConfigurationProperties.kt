package com.menta.api.users.shared.other.config.cognito

import com.menta.api.users.domain.UserType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cognito")
data class CognitoConfigurationProperties(
    val provider: Provider
) {
    data class Provider(
        val credentials: Credentials?,
        val region: String,
        val userPools: Map<UserType, UserPool>

    ) {
        data class Credentials(
            val secret: String,
            val accessKey: String
        )

        data class UserPool(
            val code: String,
            val clientId: String
        )
    }
}
