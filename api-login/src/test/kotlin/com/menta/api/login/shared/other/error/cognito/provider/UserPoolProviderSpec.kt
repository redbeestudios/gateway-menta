package com.menta.api.login.shared.other.error.cognito.provider

import arrow.core.left
import arrow.core.right
import com.menta.api.login.aUserCredentials
import com.menta.api.login.aUserPool
import com.menta.api.login.shared.other.cognito.CognitoConfigurationProperties
import com.menta.api.login.shared.other.cognito.provider.UserPoolProvider
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserPoolProviderSpec : FeatureSpec({

    val properties = mockk<CognitoConfigurationProperties>()
    val provider = UserPoolProvider(properties)

    beforeEach { clearAllMocks() }

    feature("userPoolProvider") {
        scenario("with type exist") {
            val userCredentials = aUserCredentials()
            val userPool = aUserPool()

            every { properties.provider.userPools[userCredentials.userType] } returns userPool

            provider.provideFor(userCredentials.userType) shouldBe userPool.right()

            verify(exactly = 1) { properties.provider.userPools[userCredentials.userType] }
        }
        scenario("with type not exist") {
            val userCredentials = aUserCredentials()

            every { properties.provider.userPools[userCredentials.userType] } returns null

            provider.provideFor(userCredentials.userType) shouldBe missingConfigurationForUserType(userCredentials.userType).left()

            verify(exactly = 1) { properties.provider.userPools[userCredentials.userType] }
        }
    }
})
