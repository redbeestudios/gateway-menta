package com.menta.api.login.shared.other.cognito.provider

import arrow.core.Either
import arrow.core.leftIfNull
import arrow.core.right
import com.menta.api.login.shared.domain.UserType
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import com.menta.api.login.shared.other.error.providers.ErrorResponseMetadataProvider.Companion.logEither
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
                { info("user pool for type: {}", userType, it) }
            )
}
