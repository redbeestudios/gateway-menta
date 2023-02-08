package com.kiwi.api.reimbursements.hexagonal.application.usecase

import com.kiwi.api.reimbursements.hexagonal.application.aCustomer
import com.kiwi.api.reimbursements.hexagonal.application.port.out.CustomerRepositoryPortOut
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindCustomerUseCaseSpec : FeatureSpec({

    feature("customer search") {

        lateinit var customerRepository: CustomerRepositoryPortOut

        lateinit var useCase: FindCustomerUseCase

        beforeEach {
            customerRepository = mockk()

            useCase = FindCustomerUseCase(
                customerRepository = customerRepository
            )
        }

        scenario("successful customer search") {
            val customer = aCustomer()
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { customerRepository.retrieve(customerId) } returns customer

            // expect that
            useCase.execute(customerId) shouldBe customer

            // dependencies called
            verify(exactly = 1) { customerRepository.retrieve(customerId) }
        }

        scenario("customer not FOUND") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { customerRepository.retrieve(customerId) } throws InternalError("Customer not found")

            // expect that
            shouldThrow<InternalError> { useCase.execute(customerId) }

            // dependencies called
            verify(exactly = 1) { customerRepository.retrieve(customerId) }
        }
    }
})
