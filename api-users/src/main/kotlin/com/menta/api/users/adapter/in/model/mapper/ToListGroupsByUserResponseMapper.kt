package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.adapter.`in`.model.ListGroupsByUserResponse
import com.menta.api.users.adapter.`in`.model.ListGroupsByUserResponse.SearchMetadata
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListGroupsByUserResponseMapper(
    private val toUserResponseMapper: ToUserResponseMapper
) {

    fun mapFrom(userWithGroups: UserWithGroups) =
        with(userWithGroups) {
            ListGroupsByUserResponse(
                user = user.map(),
                groups = groups.map {
                    ListGroupsByUserResponse.Group(
                        name = it.name, description = it.description,
                        audit = ListGroupsByUserResponse.Group.Audit(
                            creationDate = it.audit.creationDate,
                            updateDate = it.audit.updateDate
                        )
                    )
                },
                _metadata = SearchMetadata(
                    _next = next,
                    _limit = limit
                )
            )
        }.log { info("response mapped: {}", it) }

    private fun User.map() =
        toUserResponseMapper.mapFrom(this)
            .log { info("user response mapped: {}", it) }

    companion object : CompanionLogger()
}
