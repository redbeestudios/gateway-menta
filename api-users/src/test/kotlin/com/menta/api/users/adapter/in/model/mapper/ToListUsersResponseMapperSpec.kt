package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.aUser
import com.menta.api.users.aUserResponse
import com.menta.api.users.adapter.`in`.model.ListUsersResponse
import com.menta.api.users.domain.ListUserPage
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToListUsersResponseMapperSpec : FeatureSpec({

    val userMapper = mockk<ToUserResponseMapper>()
    val mapper = ToListUsersResponseMapper(toUserResponseMapper = userMapper)

    beforeEach { clearAllMocks() }

    feature("map response from user with groups") {

        scenario("successful map") {
            val user = aUser()
            val userResponse = aUserResponse()
            val listUserPage = ListUserPage(
                users = listOf(
                    user
                ),
                next = null,
                limit = null
            )

            every { userMapper.mapFrom(user) } returns userResponse

            mapper.mapFrom(listUserPage) shouldBe
                ListUsersResponse(
                    users = listOf(userResponse),
                    _metadata = ListUsersResponse.SearchMetadata(
                        _next = null,
                        _limit = null
                    )
                )
        }
    }
})
