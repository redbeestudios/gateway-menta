package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.shared.other.error.model.ApplicationError

interface ListGroupsByUserPortIn {
    fun list(query: ListGroupByUserQuery): Either<ApplicationError, UserWithGroups>
}
