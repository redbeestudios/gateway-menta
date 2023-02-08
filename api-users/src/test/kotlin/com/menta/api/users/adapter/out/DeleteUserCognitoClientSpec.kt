package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aDeleteUserRequest
import com.menta.api.users.aDeleteUserResponse
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToAdminDisableUserRequestMapper
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

class DeleteUserCognitoClientSpec : FeatureSpec({

    feature("delete user by email and pool") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toAdminDisableUserRequestMapper = mockk<ToAdminDisableUserRequestMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = DeleteUserCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toAdminDisableUserRequestMapper = toAdminDisableUserRequestMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        scenario("user exist") {
            val email = email
            val type = MERCHANT
            val pool = aUserPool()
            val request = aDeleteUserRequest()
            val response = aDeleteUserResponse()

            every { userPoolProvider.provideFor(type) } returns pool.right()
            every { toAdminDisableUserRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.adminDisableUser(request) } returns response

            client.deleteBy(email, type) shouldBeRight Unit

            verify(exactly = 1) { userPoolProvider.provideFor(type) }
            verify(exactly = 1) { toAdminDisableUserRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminDisableUser(request) }
        }

        scenario("error communicating with cognito") {
            val email = email
            val type = MERCHANT
            val pool = aUserPool()
            val request = aDeleteUserRequest()
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(type) } returns pool.right()
            every { toAdminDisableUserRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.adminDisableUser(request) } throws ex

            client.deleteBy(email, type) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(type) }
            verify(exactly = 1) { toAdminDisableUserRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminDisableUser(request) }
        }

        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val email = email
                val type = MERCHANT
                val pool = aUserPool()
                val request = aDeleteUserRequest()

                every { userPoolProvider.provideFor(type) } returns pool.right()
                every { toAdminDisableUserRequestMapper.mapFrom(email, pool) } returns request
                every { awsCognitoIdentityProvider.adminDisableUser(request) } throws ex

                client.deleteBy(email, type) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(type) }
                verify(exactly = 1) { toAdminDisableUserRequestMapper.mapFrom(email, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminDisableUser(request) }
            }
        }
    }
})
