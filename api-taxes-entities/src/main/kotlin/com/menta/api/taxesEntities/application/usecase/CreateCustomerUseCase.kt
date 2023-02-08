package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateCustomerUseCase(
    private val taxCustomerRepository: TaxCustomerRepositoryOutPort
) : CreateTaxCustomerPortIn {

    override fun execute(taxCustomer: TaxCustomer): Either<ApplicationError, TaxCustomer> =
        taxCustomer.save()

    private fun TaxCustomer.save() =
        taxCustomerRepository.create(this)
            .logRight { info("Tax Customer {} created with id {}", it.customerId, it.id) }

    companion object : CompanionLogger()
}
