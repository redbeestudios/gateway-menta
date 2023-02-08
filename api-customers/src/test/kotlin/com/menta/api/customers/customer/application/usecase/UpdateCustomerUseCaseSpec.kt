package com.menta.api.customers.customer.application.usecase

import arrow.core.right
import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.anUpdatedCustomer
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.resolver.CustomerUpdateResolver
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UpdateCustomerUseCaseSpec : FeatureSpec({

    val resolver = mockk<CustomerUpdateResolver>()
    val repository = mockk<CustomerRepositoryOutPort>()
    val useCase = UpdateCustomerUseCase(repository, resolver)

    beforeEach { clearAllMocks() }

    feature("update customer") {

        scenario("with customer NOT deleted") {

            every { resolver.resolveUpdate(aCustomerCreated) } returns anUpdatedCustomer
            every { repository.update(anUpdatedCustomer) } returns anUpdatedCustomer.right()

            useCase.execute(aCustomerCreated) shouldBeRight anUpdatedCustomer

            verify(exactly = 1) { resolver.resolveUpdate(aCustomerCreated) }
            verify(exactly = 1) { repository.update(anUpdatedCustomer) }
        }
    }
})
