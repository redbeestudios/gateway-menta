package com.menta.api.taxesEntities.taxCustomer.usecase

import arrow.core.right
import com.menta.api.taxesEntities.TestConstants.Companion.CUSTOMER_ID
import com.menta.api.taxesEntities.aPreTaxCustomer
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.UpdateCustomerUseCase
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxCustomerDoesNotExists
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class UpdateCustomerUseCaseSpec : FeatureSpec({

    val repository: TaxCustomerRepositoryOutPort = mockk()
    val useCase: UpdateCustomerUseCase = UpdateCustomerUseCase(
        customerRepository = repository
    )

    beforeEach { clearAllMocks() }

    feature("update tax customer") {

        scenario("Ok") {
            val customerId = UUID.fromString(CUSTOMER_ID)
            val preTaxCustomer = aPreTaxCustomer()
            val taxCustomer = aTaxCustomer()

            every { repository.findBy(customerId) } returns Optional.of(taxCustomer)
            every { repository.update(taxCustomer) } returns taxCustomer.right()

            useCase.execute(preTaxCustomer, customerId) shouldBeRight taxCustomer

            verify(exactly = 1) { repository.findBy(customerId) }
            verify(exactly = 1) { repository.update(taxCustomer) }
        }

        scenario("tax customer not found") {
            val customerId = UUID.fromString(CUSTOMER_ID)
            val preTaxCustomer = aPreTaxCustomer()
            val taxCustomer = aTaxCustomer()

            every { repository.findBy(customerId) } returns Optional.empty()
            useCase.execute(preTaxCustomer, customerId) shouldBeLeft taxCustomerDoesNotExists()

            verify(exactly = 1) { repository.findBy(customerId) }
            verify(exactly = 0) { repository.update(taxCustomer) }
        }
    }
})
