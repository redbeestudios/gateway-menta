package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.taxesEntities.adapter.`in`.controller.TaxCustomerController.Companion.log
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxCustomerPorIn
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.domain.PreTaxCustomer
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxCustomerDoesNotExists
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class UpdateCustomerUseCase(
    private val customerRepository: TaxCustomerRepositoryOutPort
) : UpdateTaxCustomerPorIn {
    override fun execute(preTaxCustomer: PreTaxCustomer, customerId: UUID): Either<ApplicationError, TaxCustomer> =
        with(preTaxCustomer) {
            get(customerId).shouldExist().flatMap {
                it.copy(
                    taxCondition = taxCondition,
                    settlementCondition = settlementCondition,
                    customerId = customerId,
                    country = country,
                    feeRules = feeRules
                )
                    .update()
                    .log { info("Tax customer updated") }
            }
        }

    private fun get(customerId: UUID) = customerRepository.findBy(customerId)

    private fun TaxCustomer.update() =
        customerRepository.update(this)

    private fun Optional<TaxCustomer>.shouldExist() =
        if (this.isEmpty) {
            taxCustomerDoesNotExists().left()
        } else {
            this.get().right()
        }
}
