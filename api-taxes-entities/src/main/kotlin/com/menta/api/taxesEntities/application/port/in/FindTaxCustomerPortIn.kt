package com.menta.api.taxesEntities.application.port.`in`

import arrow.core.Either
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.UUID

interface FindTaxCustomerPortIn {
    fun execute(customerId: UUID): Either<ApplicationError, TaxCustomer>
}
