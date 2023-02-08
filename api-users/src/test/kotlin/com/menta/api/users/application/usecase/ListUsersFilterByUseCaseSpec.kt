package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aListUserPage
import com.menta.api.users.aListUsersFilterByQuery
import com.menta.api.users.aListUsersFilterByQueryResult
import com.menta.api.users.application.port.out.ListUsersFilterByPortOut
import com.menta.api.users.domain.mapper.ToListUsersMapper
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ListUsersFilterByUseCaseSpec : FeatureSpec({

    val listUsersFilterByPortOut = mockk<ListUsersFilterByPortOut>()
    val toListUsersMapper = mockk<ToListUsersMapper>()

    val useCase = ListUsersFilterByUseCase(
        portOut = listUsersFilterByPortOut,
        toListUsersMapper = toListUsersMapper
    )

    beforeEach { clearAllMocks() }

    feature("find users filter by type") {
        scenario("users found") {
            val query = aListUsersFilterByQuery()
            val result = aListUsersFilterByQueryResult()
            val users = aListUserPage()

            every { listUsersFilterByPortOut.list(query) } returns result.right()
            every { toListUsersMapper.mapFrom(query, result) } returns users

            useCase.list(query) shouldBeRight users

            verify(exactly = 1) { listUsersFilterByPortOut.list(query) }
            verify(exactly = 1) { toListUsersMapper.mapFrom(query, result) }
        }
        scenario("error communicating with cognito") {
            val query = aListUsersFilterByQuery()
            val error = unauthorizedUser()

            every { listUsersFilterByPortOut.list(query) } returns error.left()

            useCase.list(query) shouldBeLeft error

            verify(exactly = 1) { listUsersFilterByPortOut.list(query) }
            verify(exactly = 0) { toListUsersMapper.mapFrom(any(), any()) }
        }
    }
})
