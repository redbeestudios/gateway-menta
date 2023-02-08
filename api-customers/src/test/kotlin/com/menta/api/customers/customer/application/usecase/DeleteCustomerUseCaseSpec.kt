package com.menta.api.customers.customer.application.usecase

import arrow.core.right
import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerDeleted
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.resolver.CustomerDeletionResolver
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteCustomerUseCaseSpec : FeatureSpec({

    val deletionResolver = mockk<CustomerDeletionResolver>()
    val repository = mockk<CustomerRepositoryOutPort>()
    val useCase = DeleteCustomerUseCase(repository, deletionResolver)

    beforeEach { clearAllMocks() }

    feature("delete customer") {

        scenario("with customer NOT deleted") {
            val customer = aCustomerCreated
            val customerDeleted = aCustomerDeleted()

            every { deletionResolver.resolveDeletion(customer) } returns customerDeleted
            every { repository.update(customerDeleted) } returns customerDeleted.right()

            useCase.execute(customer) shouldBeRight customerDeleted

            verify(exactly = 1) { deletionResolver.resolveDeletion(customer) }
            verify(exactly = 1) { repository.update(customerDeleted) }
        }
    }
})
