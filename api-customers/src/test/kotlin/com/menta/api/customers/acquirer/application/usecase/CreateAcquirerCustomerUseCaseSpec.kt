package com.menta.api.customers.acquirer.application.usecase

import com.menta.api.customers.aPreAcquirerCustomer
import com.menta.api.customers.acquirer.application.mapper.ToAcquirerCustomerMapper
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.anAcquirerCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class CreateAcquirerCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerCustomerRepositoryOutPort>()
    val toAcquirerCustomerMapper = mockk<ToAcquirerCustomerMapper>()
    val useCase = CreateAcquirerCustomerUseCase(repository, toAcquirerCustomerMapper)

    beforeEach { clearAllMocks() }

    feature("create acquirer customer") {

        scenario("with pre acquirer customer") {
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomer = anAcquirerCustomer()

            every { toAcquirerCustomerMapper.map(preAcquirerCustomer) } returns acquirerCustomer
            every { repository.create(acquirerCustomer) } returns acquirerCustomer

            useCase.execute(preAcquirerCustomer, Optional.empty()) shouldBeRight acquirerCustomer

            verify(exactly = 1) { toAcquirerCustomerMapper.map(preAcquirerCustomer) }
            verify(exactly = 1) { repository.create(acquirerCustomer) }
        }

        scenario("with existing customer") {
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomer = anAcquirerCustomer()

            every { toAcquirerCustomerMapper.map(preAcquirerCustomer) } returns acquirerCustomer
            every { repository.create(acquirerCustomer) } returns acquirerCustomer

            useCase.execute(
                preAcquirerCustomer,
                Optional.of(acquirerCustomer)
            ) shouldBeLeft ApplicationError.acquirerCustomerExists()

            verify(exactly = 0) { toAcquirerCustomerMapper.map(preAcquirerCustomer) }
            verify(exactly = 0) { repository.create(acquirerCustomer) }
        }
    }
})
