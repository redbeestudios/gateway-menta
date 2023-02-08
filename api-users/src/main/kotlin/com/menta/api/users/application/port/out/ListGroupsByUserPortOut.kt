package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.shared.other.error.model.ApplicationError

interface ListGroupsByUserPortOut {
    fun list(query: ListGroupByUserQuery): Either<ApplicationError, ListGroupsByUserQueryResult>
}
