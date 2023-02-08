package com.menta.apiacquirers.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.apiacquirers.aCustomer
import com.menta.apiacquirers.application.port.out.FindCustomerPortOut
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.customerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<FindCustomerPortOut>()
    val useCase = FindCustomerUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find customer") {
        val customerId = customerId

        scenario("customer found") {
            val customer = aCustomer()

            every { repository.findBy(customerId) } returns customer.right()

            useCase.execute(customerId) shouldBeRight customer

            verify(exactly = 1) { repository.findBy(customerId) }
        }
        scenario("customer NOT found") {
            val error = customerNotFound(customerId)

            every { repository.findBy(customerId) } returns error.left()

            useCase.execute(customerId) shouldBeLeft error

            verify(exactly = 1) { repository.findBy(customerId) }
        }
    }
})
