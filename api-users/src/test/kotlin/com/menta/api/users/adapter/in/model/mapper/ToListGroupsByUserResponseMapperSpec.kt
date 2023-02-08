package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.aUser
import com.menta.api.users.aUserResponse
import com.menta.api.users.adapter.`in`.model.ListGroupsByUserResponse
import com.menta.api.users.createDate
import com.menta.api.users.domain.Group
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToListGroupsByUserResponseMapperSpec : FeatureSpec({

    val userMapper = mockk<ToUserResponseMapper>()
    val mapper = ToListGroupsByUserResponseMapper(toUserResponseMapper = userMapper)

    beforeEach { clearAllMocks() }

    feature("map response from user with groups") {

        scenario("map") {
            val user = aUser()
            val userResponse = aUserResponse()
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

            every { userMapper.mapFrom(user) } returns userResponse

            mapper.mapFrom(userWithGroups) shouldBe
                ListGroupsByUserResponse(
                    user = userResponse,
                    groups = listOf(
                        ListGroupsByUserResponse.Group(
                            name = "name",
                            description = "description",
                            audit = ListGroupsByUserResponse.Group.Audit(
                                creationDate = createDate,
                                updateDate = updateDate
                            )
                        )
                    ),
                    _metadata = ListGroupsByUserResponse.SearchMetadata(
                        _next = null,
                        _limit = null
                    )
                )
        }
    }
})
