package com.menta.apisecrets.application.port.out

import arrow.core.Either
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Country
import com.menta.apisecrets.shared.error.model.ApplicationError

interface AcquirerRepositoryOutPort {
    fun findBy(country: Country): Either<ApplicationError, Acquirer>
}
