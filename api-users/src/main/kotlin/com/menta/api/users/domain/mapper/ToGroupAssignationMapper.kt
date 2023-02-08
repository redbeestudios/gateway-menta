package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.AssignGroupRequest
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToGroupAssignationMapper {

    fun mapFrom(request: AssignGroupRequest, user: User): GroupAssignation =
        with(request) {
            GroupAssignation(
                user = user,
                group = GroupAssignation.Group(
                    name = name
                )
            )
        }.log { info("group assignation mapped: {}", it) }

    companion object : CompanionLogger()
}
