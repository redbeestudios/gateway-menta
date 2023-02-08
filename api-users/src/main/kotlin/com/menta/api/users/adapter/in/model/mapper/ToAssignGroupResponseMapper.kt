package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.adapter.`in`.model.AssignGroupResponse
import com.menta.api.users.adapter.`in`.model.AssignGroupResponse.Group
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAssignGroupResponseMapper(
    private val toUseResponseMapper: ToUserResponseMapper
) {

    fun mapFrom(groupAssignation: GroupAssignation): AssignGroupResponse =
        with(groupAssignation) {
            AssignGroupResponse(
                user = user.map(),
                group = Group(
                    name = group.name
                )
            )
        }

    private fun User.map() =
        toUseResponseMapper.mapFrom(this)
            .log { info("user response mapped: {}", it) }

    companion object : CompanionLogger()
}
