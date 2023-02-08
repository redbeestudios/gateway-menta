package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aFindUserRequest
import com.menta.api.users.aFindUserResponse
import com.menta.api.users.aUser
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToAdminGetUserRequestMapper
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.mapper.ToUserMapper
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

class FindUserCognitoClientSpec : FeatureSpec({

    feature("find user by email and pool") {

        val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
        val toAdminGetUserRequestMapper = mockk<ToAdminGetUserRequestMapper>()
        val toUserMapper = mockk<ToUserMapper>()
        val userPoolProvider = mockk<UserPoolProvider>()

        val client = FindUserCognitoClient(
            awsCognitoIdentityProvider = awsCognitoIdentityProvider,
            toAdminGetUserRequestMapper = toAdminGetUserRequestMapper,
            toUserMapper = toUserMapper,
            userPoolProvider = userPoolProvider
        )

        beforeEach { clearAllMocks() }

        scenario("user found") {
            val email = email
            val pool = aUserPool()
            val request = aFindUserRequest()
            val response = aFindUserResponse()
            val user = aUser()

            every { userPoolProvider.provideFor(user.attributes.type) } returns pool.right()
            every { toAdminGetUserRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.adminGetUser(request) } returns response
            every { toUserMapper.mapFrom(response, user.attributes.type) } returns user

            client.findBy(email, user.attributes.type) shouldBeRight user

            verify(exactly = 1) { userPoolProvider.provideFor(user.attributes.type) }
            verify(exactly = 1) { toAdminGetUserRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminGetUser(request) }
            verify(exactly = 1) { toUserMapper.mapFrom(response, user.attributes.type) }
        }

        scenario("error communicating with cognito") {
            val email = email
            val type = MERCHANT
            val pool = aUserPool()
            val request = aFindUserRequest()
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)

            every { userPoolProvider.provideFor(type) } returns pool.right()
            every { toAdminGetUserRequestMapper.mapFrom(email, pool) } returns request
            every { awsCognitoIdentityProvider.adminGetUser(request) } throws ex

            client.findBy(email, type) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(type) }
            verify(exactly = 1) { toAdminGetUserRequestMapper.mapFrom(email, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminGetUser(request) }
            verify(exactly = 0) { toUserMapper.mapFrom(any<AdminGetUserResult>(), any()) }
        }

        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val email = email
                val type = MERCHANT
                val pool = aUserPool()
                val request = aFindUserRequest()

                every { userPoolProvider.provideFor(type) } returns pool.right()
                every { toAdminGetUserRequestMapper.mapFrom(email, pool) } returns request
                every { awsCognitoIdentityProvider.adminGetUser(request) } throws ex

                client.findBy(email, type) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(type) }
                verify(exactly = 1) { toAdminGetUserRequestMapper.mapFrom(email, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminGetUser(request) }
                verify(exactly = 0) { toUserMapper.mapFrom(any<AdminGetUserResult>(), any()) }
            }
        }
    }
})
