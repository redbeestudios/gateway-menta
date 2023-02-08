package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aConfirmForgotPasswordRequest
import com.menta.api.users.aConfirmForgotPasswordResponse
import com.menta.api.users.aConfirmForgotPasswordUser
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToConfirmForgotPasswordRequestMapper
import com.menta.api.users.exceptionToApplicationErrorTable
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ConfirmForgotPasswordCognitoClientSpec : FeatureSpec({

    feature("confirm forgot password user by email and type") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toConfirmForgotPasswordRequestMapper = mockk<ToConfirmForgotPasswordRequestMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = ConfirmForgotPasswordCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toConfirmForgotPasswordRequestMapper = toConfirmForgotPasswordRequestMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        val pool = aUserPool()
        val confirmForgot = aConfirmForgotPasswordUser()
        val request = aConfirmForgotPasswordRequest()

        scenario("with user exist") {
            val response = aConfirmForgotPasswordResponse()

            every { userPoolProvider.provideFor(confirmForgot.type) } returns pool.right()
            every { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) } returns request
            every { awsCognitoIdentityProvider.confirmForgotPassword(request) } returns response

            client.confirm(confirmForgot) shouldBeRight Unit

            verify(exactly = 1) { userPoolProvider.provideFor(confirmForgot.type) }
            verify(exactly = 1) { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.confirmForgotPassword(request) }
        }
        scenario("error communicating with cognito") {
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(confirmForgot.type) } returns pool.right()
            every { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) } returns request
            every { awsCognitoIdentityProvider.confirmForgotPassword(request) } throws ex

            client.confirm(confirmForgot) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(confirmForgot.type) }
            verify(exactly = 1) { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.confirmForgotPassword(request) }
        }
        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                every { userPoolProvider.provideFor(confirmForgot.type) } returns pool.right()
                every { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) } returns request
                every { awsCognitoIdentityProvider.confirmForgotPassword(request) } throws ex

                client.confirm(confirmForgot) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(confirmForgot.type) }
                verify(exactly = 1) { toConfirmForgotPasswordRequestMapper.mapFrom(confirmForgot, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.confirmForgotPassword(request) }
            }
        }
    }
})
