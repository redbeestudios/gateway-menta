package com.menta.api.users.adapter.out

import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest
import com.menta.api.users.aAdminListGroupsForUserResult
import com.menta.api.users.aListGroupByUserQuery
import com.menta.api.users.aListGroupsByUserQueryResult
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.`in`.model.mapper.ToAdminListGroupForUserRequestMapper
import com.menta.api.users.domain.mapper.ToListGroupsByUserQueryResultMapper
import com.menta.api.users.exceptionToApplicationErrorTable
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ListGroupsByUserCognitoClientSpec : FeatureSpec({
    val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
    val toAdminListGroupForUserRequestMapper = mockk<ToAdminListGroupForUserRequestMapper>()
    val toListGroupsByUserQueryResultMapper = mockk<ToListGroupsByUserQueryResultMapper>()
    val userPoolProvider = mockk<UserPoolProvider>()

    val client = ListGroupsByUserCognitoClient(
        awsCognitoIdentityProvider = awsCognitoIdentityProvider,
        toAdminListGroupForUserRequestMapper = toAdminListGroupForUserRequestMapper,
        toListGroupsByUserQueryResultMapper = toListGroupsByUserQueryResultMapper,
        userPoolProvider = userPoolProvider
    )

    beforeEach { clearAllMocks() }

    feature("list group by user") {

        scenario("filter by user") {
            val pool = aUserPool()
            val query = aListGroupByUserQuery()
            val request = AdminListGroupsForUserRequest()
                .withUserPoolId(pool.code)
                .withUsername(query.user.attributes.email)
            val result = aAdminListGroupsForUserResult()
            val listGroups = aListGroupsByUserQueryResult()

            every { userPoolProvider.provideFor(query.user.attributes.type) } returns pool.right()
            every { toAdminListGroupForUserRequestMapper.mapFrom(query, pool) } returns request
            every { awsCognitoIdentityProvider.adminListGroupsForUser(request) } returns result
            every { toListGroupsByUserQueryResultMapper.mapFrom(result) } returns listGroups

            client.list(query) shouldBeRight listGroups

            verify(exactly = 1) { userPoolProvider.provideFor(query.user.attributes.type) }
            verify(exactly = 1) { toAdminListGroupForUserRequestMapper.mapFrom(query, pool) }
            verify(exactly = 1) { awsCognitoIdentityProvider.adminListGroupsForUser(request) }
            verify(exactly = 1) { toListGroupsByUserQueryResultMapper.mapFrom(result) }
        }
        scenario("cognito throws error") {
            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()
                val pool = aUserPool()
                val query = aListGroupByUserQuery()
                val request = AdminListGroupsForUserRequest()
                    .withUserPoolId(pool.code)
                    .withUsername(query.user.attributes.email)

                every { userPoolProvider.provideFor(query.user.attributes.type) } returns pool.right()
                every { toAdminListGroupForUserRequestMapper.mapFrom(query, pool) } returns request
                every { awsCognitoIdentityProvider.adminListGroupsForUser(request) } throws ex

                client.list(query) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(query.user.attributes.type) }
                verify(exactly = 1) { toAdminListGroupForUserRequestMapper.mapFrom(query, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminListGroupsForUser(request) }
            }
        }
        scenario("pool not found") {
            val query = aListGroupByUserQuery()
            val error = missingConfigurationForUserType(query.user.attributes.type)

            every { userPoolProvider.provideFor(query.user.attributes.type) } returns error.left()

            client.list(query) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(query.user.attributes.type) }
            verify(exactly = 0) { toAdminListGroupForUserRequestMapper.mapFrom(any(), any()) }
            verify(exactly = 0) { awsCognitoIdentityProvider.adminListGroupsForUser(any()) }
        }
    }
})
