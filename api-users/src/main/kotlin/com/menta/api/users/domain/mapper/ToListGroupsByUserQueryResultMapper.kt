package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult
import com.menta.api.users.domain.Group
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToListGroupsByUserQueryResultMapper {
    fun mapFrom(result: AdminListGroupsForUserResult) =
        with(result) {
            ListGroupsByUserQueryResult(
                groups = groups.map {
                    Group(
                        name = it.groupName,
                        description = it.description,
                        audit = Group.Audit(creationDate = it.creationDate, updateDate = it.lastModifiedDate)
                    )
                },
                next = nextToken
            )
        }.log { info("query result mapped from cognito result: {}", it) }

    companion object : CompanionLogger()
}
