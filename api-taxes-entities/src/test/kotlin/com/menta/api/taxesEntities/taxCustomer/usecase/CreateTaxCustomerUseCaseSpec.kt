package com.menta.api.taxesEntities.taxCustomer.usecase

import arrow.core.right
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.CreateCustomerUseCase
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateTaxCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<TaxCustomerRepositoryOutPort>()
    val useCase = CreateCustomerUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("create tax customer") {
        scenario("with tax customer valid") {
            val taxCustomer = aTaxCustomer().copy(id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"))

            every { repository.create(taxCustomer) } returns taxCustomer.right()

            useCase.execute(taxCustomer) shouldBeRight taxCustomer

            verify(exactly = 1) { repository.create(taxCustomer) }
        }
    }
})
