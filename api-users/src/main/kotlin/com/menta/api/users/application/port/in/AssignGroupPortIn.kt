package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.shared.other.error.model.ApplicationError

interface AssignGroupPortIn {
    fun assign(groupAssignation: GroupAssignation): Either<ApplicationError, GroupAssignation>
}
