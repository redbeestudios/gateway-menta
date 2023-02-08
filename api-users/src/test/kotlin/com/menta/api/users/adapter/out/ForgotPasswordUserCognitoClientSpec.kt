package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aForgotPasswordUserRequest
import com.menta.api.users.aForgotPasswordUserResponse
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToForgotPasswordRequestMapper
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
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

class ForgotPasswordUserCognitoClientSpec : FeatureSpec({

    feature("forgot password user by email and type") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toForgotPasswordRequestMapper = mockk<ToForgotPasswordRequestMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = ForgotPasswordCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toForgotPasswordRequestMapper = toForgotPasswordRequestMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        scenario("with user exist") {
            val email = email
            val type = MERCHANT
            val pool = aUserPool()
            val request = aForgotPasswordUserRequest()
            val response = aForgotPasswordUserResponse()

            every { userPoolProvider.provideFor(type) } returns pool.right()
            every { toForgotPasswordRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.forgotPassword(request) } returns response

            client.retrieve(email, type) shouldBeRight Unit

            verify(exactly = 1) { userPoolProvider.provideFor(type) }
            verify(exactly = 1) { toForgotPasswordRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.forgotPassword(request) }
        }
        scenario("error communicating with cognito") {
            val email = email
            val type = MERCHANT
            val pool = aUserPool()
            val request = aForgotPasswordUserRequest()
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(type) } returns pool.right()
            every { toForgotPasswordRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.forgotPassword(request) } throws ex

            client.retrieve(email, type) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(type) }
            verify(exactly = 1) { toForgotPasswordRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.forgotPassword(request) }
        }
        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val email = email
                val type = MERCHANT
                val pool = aUserPool()
                val request = aForgotPasswordUserRequest()

                every { userPoolProvider.provideFor(type) } returns pool.right()
                every { toForgotPasswordRequestMapper.mapFrom(email, pool) } returns request
                every { awsCognitoIdentityProvider.forgotPassword(request) } throws ex

                client.retrieve(email, type) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(type) }
                verify(exactly = 1) { toForgotPasswordRequestMapper.mapFrom(email, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.forgotPassword(request) }
            }
        }
    }
})
