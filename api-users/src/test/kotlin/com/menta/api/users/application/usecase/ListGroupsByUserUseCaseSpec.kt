package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aListGroupByUserQuery
import com.menta.api.users.aListGroupsByUserQueryResult
import com.menta.api.users.aUser
import com.menta.api.users.application.port.out.ListGroupsByUserPortOut
import com.menta.api.users.createDate
import com.menta.api.users.domain.Group
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.domain.mapper.ToUserWithGroupsMapper
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.users.updateDate
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ListGroupsByUserUseCaseSpec : FeatureSpec({

    val listGroupsByUserPortOut = mockk<ListGroupsByUserPortOut>()
    val toUserWithGroupsMapper = mockk<ToUserWithGroupsMapper>()

    val useCase = ListGroupsByUserUseCase(
        portOut = listGroupsByUserPortOut,
        toUserWithGroupsMapper = toUserWithGroupsMapper
    )

    beforeEach { clearAllMocks() }

    feature("find list group by user") {
        scenario("groups found") {
            val query = aListGroupByUserQuery()
            val listGroups = aListGroupsByUserQueryResult()
            val userWithGroups = UserWithGroups(
                user = aUser(),
                groups = listOf(
                    Group(
                        name = "name",
                        description = "description",
                        audit = Group.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
                ),
                next = null,
                limit = null
            )

            every { listGroupsByUserPortOut.list(query) } returns listGroups.right()
            every { toUserWithGroupsMapper.mapFrom(query, listGroups) } returns userWithGroups

            useCase.list(query) shouldBeRight userWithGroups

            verify(exactly = 1) { listGroupsByUserPortOut.list(query) }
            verify(exactly = 1) { toUserWithGroupsMapper.mapFrom(query, listGroups) }
        }
        scenario("error communicating with cognito") {
            val query = aListGroupByUserQuery()
            val error = unauthorizedUser()

            every { listGroupsByUserPortOut.list(query) } returns error.left()

            useCase.list(query) shouldBeLeft error

            verify(exactly = 1) { listGroupsByUserPortOut.list(query) }
            verify(exactly = 0) { toUserWithGroupsMapper.mapFrom(any(), any()) }
        }
    }
})
