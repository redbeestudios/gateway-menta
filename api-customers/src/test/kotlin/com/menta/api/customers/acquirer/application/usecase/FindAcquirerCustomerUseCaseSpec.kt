package com.menta.api.customers.acquirer.application.usecase

import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.anAcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
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

class FindAcquirerCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerCustomerRepositoryOutPort>()
    val useCase = FindAcquirerCustomerUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find acquirer customer") {

        scenario("acquirer customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "an acquirer"
            val acquirerCustomer = anAcquirerCustomer()
            every { repository.findBy(customerId, acquirerId) } returns Optional.of(acquirerCustomer)

            useCase.execute(customerId, acquirerId) shouldBeRight acquirerCustomer

            verify(exactly = 1) { repository.findBy(customerId, acquirerId) }
        }

        scenario("acquirer customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "an acquirer"
            val error = acquirerCustomerNotFound(customerId, acquirerId)
            every { repository.findBy(customerId, acquirerId) } returns Optional.empty()

            useCase.execute(customerId, acquirerId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(customerId, acquirerId) }
        }

        scenario("find by customerId and acquirerId") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "an acquirer"
            val acquirerCustomer = anAcquirerCustomer()
            every { repository.findBy(customerId, acquirerId) } returns Optional.of(acquirerCustomer)

            useCase.find(customerId, acquirerId) shouldBe Optional.of(acquirerCustomer)

            verify(exactly = 1) { repository.findBy(customerId, acquirerId) }
        }
    }
})
