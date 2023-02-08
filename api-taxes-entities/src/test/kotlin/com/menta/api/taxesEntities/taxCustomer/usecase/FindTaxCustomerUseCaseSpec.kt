package com.menta.api.taxesEntities.taxCustomer.usecase

import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.FindCustomerUseCase
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class FindTaxCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<TaxCustomerRepositoryOutPort>()
    val useCase = FindCustomerUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find customer") {

        scenario("customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val taxCustomer = aTaxCustomer()
            every { repository.findBy(customerId) } returns Optional.of(taxCustomer)

            useCase.execute(customerId) shouldBeRight taxCustomer

            verify(exactly = 1) { repository.findBy(customerId) }
        }

        scenario("customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val error = notFound(customerId)
            every { repository.findBy(customerId) } returns Optional.empty()

            useCase.execute(customerId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(customerId) }
        }
    }
})
