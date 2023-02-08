package com.menta.api.users.adapter.out

import arrow.core.Either
import arrow.core.left
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest
import com.menta.api.users.adapter.out.model.mapper.ToAdminAddUserToGroupRequestMapper
import com.menta.api.users.application.port.out.AssignGroupPortOut
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.shared.other.config.cognito.provider.UserPoolProvider
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.handleCognitoError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.log.benchmark
import org.springframework.stereotype.Component

@Component
class AssignGroupCognitoClient(
    private val awsCognitoIdentityProvider: AWSCognitoIdentityProvider,
    private val toAdminAddUserToGroupRequestMapper: ToAdminAddUserToGroupRequestMapper,
    private val userPoolProvider: UserPoolProvider
) : AssignGroupPortOut {

    override fun assign(groupAssignation: GroupAssignation): Either<ApplicationError, GroupAssignation> =
        log.benchmark("assign group to user") {
            try {
                groupAssignation.toRequest().map {
                    it.doAssign()
                        .let { groupAssignation }
                }
            } catch (e: Throwable) {
                e.handleCognitoError(groupAssignation.user.attributes.email).left()
            }
        }

    private fun GroupAssignation.toRequest() =
        userPoolProvider.provideFor(user.attributes.type).map {
            toAdminAddUserToGroupRequestMapper.mapFrom(this, it)
        }.log { info("request mapped: {}", it) }

    private fun AdminAddUserToGroupRequest.doAssign() =
        awsCognitoIdentityProvider.adminAddUserToGroup(this)
            .log { info("group assigned: {}", it) }

    companion object : CompanionLogger()
}
