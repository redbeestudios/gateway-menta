package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminAddUserToGroupRequestMapper {

    fun mapFrom(groupAssignation: GroupAssignation, userPool: UserPool): AdminAddUserToGroupRequest =
        with(groupAssignation) {
            AdminAddUserToGroupRequest()
                .withUserPoolId(userPool.code)
                .withUsername(user.attributes.email)
                .withGroupName(group.name)
        }.log { info("request mapped: {}", it) }

    companion object : CompanionLogger()
}
