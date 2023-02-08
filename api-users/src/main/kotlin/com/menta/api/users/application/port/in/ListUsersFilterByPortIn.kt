package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.shared.other.error.model.ApplicationError

interface ListUsersFilterByPortIn {
    fun list(query: ListUsersFilterByQuery): Either<ApplicationError, ListUserPage>
}
