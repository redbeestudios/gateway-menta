package com.menta.api.taxesEntities.application.port.out

import arrow.core.Either
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface TaxCustomerRepositoryOutPort {
    fun findBy(customerId: UUID): Optional<TaxCustomer>
    fun create(taxCustomer: TaxCustomer): Either<ApplicationError, TaxCustomer>
    fun update(taxCustomer: TaxCustomer): Either<ApplicationError, TaxCustomer>
}
