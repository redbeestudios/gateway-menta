package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aAdminSetUserPasswordRequest
import com.menta.api.users.aAdminSetUserPasswordResponse
import com.menta.api.users.aSetUserPassword
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToAdminSetUserPasswordRequestMapper
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

class SetUserPasswordCognitoClientSpec : FeatureSpec({

    feature("set admin user password") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toAdminSetUserPasswordRequestMapper = mockk<ToAdminSetUserPasswordRequestMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = SetUserPasswordCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toAdminSetUserPasswordRequestMapper = toAdminSetUserPasswordRequestMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        scenario("user exist") {
            val pool = aUserPool()
            val setUserPassword = aSetUserPassword()
            val request = aAdminSetUserPasswordRequest()
            val response = aAdminSetUserPasswordResponse()

            every { userPoolProvider.provideFor(setUserPassword.type) } returns pool.right()
            every { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) } returns request
            every { awsCognitoIdentityProvider.adminSetUserPassword(request) } returns response

            client.setPassword(setUserPassword) shouldBeRight Unit

            verify(exactly = 1) { userPoolProvider.provideFor(setUserPassword.type) }
            verify(exactly = 1) { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminSetUserPassword(request) }
        }
        scenario("error communicating with cognito") {
            val pool = aUserPool()
            val setUserPassword = aSetUserPassword()
            val request = aAdminSetUserPasswordRequest()
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(setUserPassword.type) } returns pool.right()
            every { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) } returns request
            every { awsCognitoIdentityProvider.adminSetUserPassword(request) } throws ex

            client.setPassword(setUserPassword) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(setUserPassword.type) }
            verify(exactly = 1) { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminSetUserPassword(request) }
        }
        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val pool = aUserPool()
                val setUserPassword = aSetUserPassword()
                val request = aAdminSetUserPasswordRequest()

                every { userPoolProvider.provideFor(setUserPassword.type) } returns pool.right()
                every { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) } returns request
                every { awsCognitoIdentityProvider.adminSetUserPassword(request) } throws ex

                client.setPassword(setUserPassword) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(setUserPassword.type) }
                verify(exactly = 1) { toAdminSetUserPasswordRequestMapper.mapFrom(setUserPassword, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminSetUserPassword(request) }
            }
        }
    }
})
