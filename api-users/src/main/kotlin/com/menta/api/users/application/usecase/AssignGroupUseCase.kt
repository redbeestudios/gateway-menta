package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.AssignGroupPortIn
import com.menta.api.users.application.port.out.AssignGroupPortOut
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AssignGroupUseCase(
    private val assignGroupPortOut: AssignGroupPortOut
) : AssignGroupPortIn {

    override fun assign(groupAssignation: GroupAssignation): Either<ApplicationError, GroupAssignation> =
        assignGroupPortOut.assign(groupAssignation)
            .logRight { info("group assigned: {}", it) }

    companion object : CompanionLogger()
}
