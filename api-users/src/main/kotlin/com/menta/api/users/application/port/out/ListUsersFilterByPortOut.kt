package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.shared.other.error.model.ApplicationError

interface ListUsersFilterByPortOut {
    fun list(query: ListUsersFilterByQuery): Either<ApplicationError, ListUsersFilterByQueryResult>
}
