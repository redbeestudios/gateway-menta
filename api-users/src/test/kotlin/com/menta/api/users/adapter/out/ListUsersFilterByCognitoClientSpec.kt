package com.menta.api.users.adapter.out

import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.menta.api.users.aListUsersRequest
import com.menta.api.users.aListUsersResponse
import com.menta.api.users.aUser
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToListUsersRequestMapper
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.mapper.ToListUsersFilterByQueryResultMapper
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

class ListUsersFilterByCognitoClientSpec : FeatureSpec({
    val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
    val toListUsersRequestMapper = mockk<ToListUsersRequestMapper>()
    val toListUsersFilterByQueryResultMapper = mockk<ToListUsersFilterByQueryResultMapper>()
    val userPoolProvider = mockk<UserPoolProvider>()

    val client = ListUsersFilterByCognitoClient(
        awsCognitoIdentityProvider = awsCognitoIdentityProvider,
        toListUsersRequestMapper = toListUsersRequestMapper,
        toListUsersFilterByQueryResultMapper = toListUsersFilterByQueryResultMapper,
        userPoolProvider = userPoolProvider
    )

    beforeEach { clearAllMocks() }

    feature("list users filter by") {
        scenario("filter by type") {
            val pool = aUserPool()
            val query = ListUsersFilterByQuery(
                type = UserType.MERCHANT,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
            val request = aListUsersRequest()
            val response = aListUsersResponse()
            val users = listOf(aUser())
            val result = ListUsersFilterByQueryResult(
                users = users,
                next = null
            )

            every { userPoolProvider.provideFor(query.type) } returns pool.right()
            every { toListUsersRequestMapper.mapFrom(query, pool) } returns request
            every { awsCognitoIdentityProvider.listUsers(request) } returns response
            every { toListUsersFilterByQueryResultMapper.mapFrom(response, query.type) } returns result

            client.list(query) shouldBeRight result

            verify(exactly = 1) { userPoolProvider.provideFor(query.type) }
            verify(exactly = 1) { toListUsersRequestMapper.mapFrom(query, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.listUsers(request) }
            verify(exactly = 1) { toListUsersFilterByQueryResultMapper.mapFrom(response, query.type) }
        }
        scenario("error communicating with cognito") {
            val ex = InvalidParameterException("message")
            val error = unauthorizedUser(ex)
            val pool = aUserPool()
            val query = ListUsersFilterByQuery(
                type = UserType.MERCHANT,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
            val request = aListUsersRequest()

            every { userPoolProvider.provideFor(query.type) } returns pool.right()
            every { toListUsersRequestMapper.mapFrom(query, pool) } returns request
            every { awsCognitoIdentityProvider.listUsers(request) } throws ex

            client.list(query) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(query.type) }
            verify(exactly = 1) { toListUsersRequestMapper.mapFrom(query, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.listUsers(request) }
            verify(exactly = 0) { toListUsersFilterByQueryResultMapper.mapFrom(any(), any()) }
        }
        scenario("cognito returns error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()

                val pool = aUserPool()
                val query = ListUsersFilterByQuery(
                    type = UserType.MERCHANT,
                    email = email,
                    customerId = null,
                    merchantId = null,
                    search = ListUsersFilterByQuery.Search(
                        limit = null,
                        next = null
                    )
                )
                val request = aListUsersRequest()

                every { userPoolProvider.provideFor(query.type) } returns pool.right()
                every { toListUsersRequestMapper.mapFrom(query, pool) } returns request
                every { awsCognitoIdentityProvider.listUsers(request) } throws ex

                client.list(query) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(query.type) }
                verify(exactly = 1) { toListUsersRequestMapper.mapFrom(query, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.listUsers(request) }
                verify(exactly = 0) { toListUsersFilterByQueryResultMapper.mapFrom(any(), any()) }
            }
        }
    }
})
