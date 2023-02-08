package com.menta.api.users.shared.other.config.cognito.provider

import arrow.core.Either
import arrow.core.leftIfNull
import arrow.core.right
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import com.menta.api.users.shared.other.error.providers.ErrorResponseMetadataProvider.Companion.logEither
import org.springframework.stereotype.Component

@Component
class UserPoolProvider(
    private val properties: CognitoConfigurationProperties
) {

    fun provideFor(userType: UserType): Either<ApplicationError, UserPool> =
        properties.provider.userPools[userType]
            .right()
            .leftIfNull { missingConfigurationForUserType(userType) }
            .logEither(
                { warn("Missing userPool config for user type: {}", it) },
                { info("user pool: {} and type: {}", it, userType) }
            )
}
