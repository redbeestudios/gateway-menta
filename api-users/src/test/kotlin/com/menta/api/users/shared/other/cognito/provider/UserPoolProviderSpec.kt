package com.menta.api.users.shared.other.cognito.provider

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aUserPool
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
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
            val userType = UserType.MERCHANT
            val userPool = aUserPool()

            every { properties.provider.userPools[userType] } returns userPool

            provider.provideFor(userType) shouldBe userPool.right()

            verify(exactly = 1) { properties.provider.userPools[userType] }
        }
        scenario("with type not exist") {
            val userType = UserType.MERCHANT

            every { properties.provider.userPools[userType] } returns null

            provider.provideFor(userType) shouldBe missingConfigurationForUserType(userType).left()

            verify(exactly = 1) { properties.provider.userPools[userType] }
        }
    }
})
