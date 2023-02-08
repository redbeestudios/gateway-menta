package com.menta.api.login.refresh.application.port.`in`

import arrow.core.Either
import com.menta.api.login.refresh.domain.Refresh
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError

interface RefreshInPort {
    fun refresh(refresh: Refresh): Either<ApplicationError, UserAuth>
}