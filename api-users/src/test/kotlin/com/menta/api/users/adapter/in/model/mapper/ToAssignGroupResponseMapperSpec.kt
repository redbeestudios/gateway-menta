package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.aUser
import com.menta.api.users.aUserResponse
import com.menta.api.users.adapter.`in`.model.AssignGroupResponse
import com.menta.api.users.domain.GroupAssignation
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToAssignGroupResponseMapperSpec : FeatureSpec({

    val userMapper = mockk<ToUserResponseMapper>()
    val mapper = ToAssignGroupResponseMapper(toUseResponseMapper = userMapper)

    beforeEach { clearAllMocks() }

    feature("map response from group assignation") {

        scenario("map") {
            val user = aUser()
            val userResponse = aUserResponse()
            val group = GroupAssignation(
                user = user,
                group = GroupAssignation.Group(name = "Payment::Create")
            )

            every { userMapper.mapFrom(user) } returns userResponse

            mapper.mapFrom(group) shouldBe
                AssignGroupResponse(
                    user = userResponse,
                    group = AssignGroupResponse.Group(name = "Payment::Create")
                )
        }
    }
})
