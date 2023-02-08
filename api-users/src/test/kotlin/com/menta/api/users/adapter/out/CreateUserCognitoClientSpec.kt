package com.menta.api.users.adapter.out

import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aCreateUserRequest
import com.menta.api.users.aCreateUserResponse
import com.menta.api.users.aNewUser
import com.menta.api.users.aUser
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToAdminCreateUserRequestMapper
import com.menta.api.users.domain.mapper.ToUserMapper
import com.menta.api.users.exceptionToApplicationErrorTable
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateUserCognitoClientSpec : FeatureSpec({

    feature("create user") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toAdminCreateUserRequestMapper = mockk<ToAdminCreateUserRequestMapper>()
        val toUserMapper = mockk<ToUserMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = CreateUserCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toAdminCreateUserRequestMapper = toAdminCreateUserRequestMapper,
            toUserMapper = toUserMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        scenario("user valid") {
            val newUser = aNewUser()
            val pool = aUserPool()
            val request = aCreateUserRequest()
            val response = aCreateUserResponse()
            val user = aUser()

            every { userPoolProvider.provideFor(newUser.type) } returns pool.right()
            every { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) } returns request
            every { awsCognitoIdentityProvider.adminCreateUser(request) } returns response
            every { toUserMapper.mapFrom(response, newUser.type) } returns user

            client.create(newUser) shouldBeRight user

            verify(exactly = 1) { userPoolProvider.provideFor(newUser.type) }
            verify(exactly = 1) { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminCreateUser(request) }
            verify(exactly = 1) { toUserMapper.mapFrom(response, newUser.type) }
        }

        scenario("error communicating with cognito") {
            val newUser = aNewUser()
            val pool = aUserPool()
            val request = aCreateUserRequest()
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(newUser.type) } returns pool.right()
            every { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) } returns request
            every { awsCognitoIdentityProvider.adminCreateUser(request) } throws ex

            client.create(newUser) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(newUser.type) }
            verify(exactly = 1) { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminCreateUser(request) }
            verify(exactly = 0) { toUserMapper.mapFrom(any<AdminCreateUserResult>(), any()) }
        }

        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val newUser = aNewUser()
                val pool = aUserPool()
                val request = aCreateUserRequest()

                every { userPoolProvider.provideFor(newUser.type) } returns pool.right()
                every { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) } returns request
                every { awsCognitoIdentityProvider.adminCreateUser(request) } throws ex

                client.create(newUser) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(newUser.type) }
                verify(exactly = 1) { toAdminCreateUserRequestMapper.mapFrom(newUser, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminCreateUser(request) }
                verify(exactly = 0) { toUserMapper.mapFrom(any<AdminCreateUserResult>(), any()) }
            }
        }

        scenario("user pool not found") {
            val newUser = aNewUser()
            val error = missingConfigurationForUserType(newUser.type)

            every { userPoolProvider.provideFor(newUser.type) } returns error.left()

            client.create(newUser) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(newUser.type) }
            verify(exactly = 0) { toAdminCreateUserRequestMapper.mapFrom(any(), any()) }
            verify(exactly = 0) { awsCognitoIdentityProvider.adminCreateUser(any()) }
            verify(exactly = 0) { toUserMapper.mapFrom(any<AdminCreateUserResult>(), any()) }
        }
    }
})
