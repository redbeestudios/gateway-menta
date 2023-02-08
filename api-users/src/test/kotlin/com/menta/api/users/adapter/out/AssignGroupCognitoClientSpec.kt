package com.menta.api.users.adapter.out

import arrow.core.left
import arrow.core.right
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.menta.api.users.aAdminAddUserToGroupRequest
import com.menta.api.users.aGroupAssignation
import com.menta.api.users.aUserPool
import com.menta.api.users.adapter.out.model.mapper.ToAdminAddUserToGroupRequestMapper
import com.menta.api.users.exceptionToApplicationErrorTable
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AssignGroupCognitoClientSpec : FeatureSpec({
    val awsCognitoIdentityProvider = mockk<AWSCognitoIdentityProvider>()
    val toAdminAddUserToGroupRequestMapper = mockk<ToAdminAddUserToGroupRequestMapper>()
    val userPoolProvider = mockk<UserPoolProvider>()

    val client = AssignGroupCognitoClient(
        awsCognitoIdentityProvider = awsCognitoIdentityProvider,
        toAdminAddUserToGroupRequestMapper = toAdminAddUserToGroupRequestMapper,
        userPoolProvider = userPoolProvider
    )

    beforeEach { clearAllMocks() }

    feature("assign group to user") {

        scenario("cognito throws error") {

            forAll(exceptionToApplicationErrorTable) { ex, err ->
                clearAllMocks()
                val group = aGroupAssignation()
                val pool = aUserPool()
                val request = aAdminAddUserToGroupRequest()

                every { userPoolProvider.provideFor(group.user.attributes.type) } returns pool.right()
                every { toAdminAddUserToGroupRequestMapper.mapFrom(group, pool) } returns request
                every { awsCognitoIdentityProvider.adminAddUserToGroup(request) } throws ex

                client.assign(group) shouldBeLeft err

                verify(exactly = 1) { userPoolProvider.provideFor(group.user.attributes.type) }
                verify(exactly = 1) { toAdminAddUserToGroupRequestMapper.mapFrom(group, pool) }
                verify(exactly = 1) { awsCognitoIdentityProvider.adminAddUserToGroup(request) }
            }
        }

        scenario("pool not found") {
            val group = aGroupAssignation()
            val error = ApplicationError.missingConfigurationForUserType(group.user.attributes.type)

            every { userPoolProvider.provideFor(group.user.attributes.type) } returns error.left()

            client.assign(group) shouldBeLeft error

            verify(exactly = 1) { userPoolProvider.provideFor(group.user.attributes.type) }
            verify(exactly = 0) { toAdminAddUserToGroupRequestMapper.mapFrom(any(), any()) }
            verify(exactly = 0) { awsCognitoIdentityProvider.adminAddUserToGroup(any()) }
        }
    }
})
