package com.menta.api.customers.customer.application.usecase

import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.customerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class FindCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<CustomerRepositoryOutPort>()
    val useCase = FindCustomerUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find customer by id") {

        scenario("customer found") {

            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            every { repository.findBy(customerId) } returns Optional.of(aCustomerCreated)

            useCase.execute(customerId) shouldBeRight aCustomerCreated

            verify(exactly = 1) { repository.findBy(customerId) }
        }

        scenario("customer NOT found") {

            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val error = customerNotFound(customerId)

            every { repository.findBy(customerId) } returns Optional.empty()

            useCase.execute(customerId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(customerId) }
        }
    }

    feature("find customer by univocity") {

        scenario("customer found") {

            val taxType = "a type"
            val taxId = "a tax id"

            every { repository.findBy(taxType, taxId) } returns Optional.of(aCustomerCreated)

            useCase.findByUnivocity(taxType, taxId) shouldBe Optional.of(aCustomerCreated)

            verify(exactly = 1) { repository.findBy(taxType, taxId) }
        }

        scenario("customer NOT found") {

            val taxType = "a type"
            val taxId = "a tax id"

            every { repository.findBy(taxType, taxId) } returns Optional.empty()

            useCase.findByUnivocity(taxType, taxId) shouldBe Optional.empty()

            verify(exactly = 1) { repository.findBy(taxType, taxId) }
        }
    }
})
