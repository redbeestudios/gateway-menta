package com.menta.api.customers.customer.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aPreCustomer
import com.menta.api.customers.customer.application.mapper.ToCustomerMapper
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.LegalType
import com.menta.api.customers.customer.domain.LegalTypeValidator
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class CreateCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<CustomerRepositoryOutPort>()
    val toCustomerMapper = mockk<ToCustomerMapper>()
    val validator = mockk<LegalTypeValidator>()
    val useCase = CreateCustomerUseCase(toCustomerMapper, validator, repository)

    beforeEach { clearAllMocks() }

    feature("create customer") {

        scenario("with pre customer valid") {
            val preCustomer = aPreCustomer
            val customer = aCustomerCreated

            every { toCustomerMapper.map(preCustomer) } returns customer
            every { repository.create(customer) } returns customer
            every { validator.validate(preCustomer) } returns preCustomer.right()

            useCase.execute(preCustomer, Optional.empty()) shouldBeRight customer

            verify(exactly = 1) { validator.validate(preCustomer) }
            verify(exactly = 1) { toCustomerMapper.map(preCustomer) }
            verify(exactly = 1) { repository.create(customer) }
        }

        scenario("with pre customer invalid") {
            val preCustomerInvalid = aPreCustomer
                .copy(legalType = LegalType.NATURAL_PERSON, businessOwner = null)

            every { validator.validate(preCustomerInvalid) } returns invalidBusinessOwner().left()

            useCase.execute(preCustomerInvalid, Optional.empty()) shouldBeLeft invalidBusinessOwner()

            verify(exactly = 1) { validator.validate(preCustomerInvalid) }
            verify(exactly = 0) { toCustomerMapper.map(preCustomerInvalid) }
            verify(exactly = 0) { repository.create(any()) }
        }

        scenario("with existing customer") {
            val preCustomer = aPreCustomer
            val customer = aCustomerCreated

            every { toCustomerMapper.map(preCustomer) } returns customer
            every { repository.create(customer) } returns customer
            every { validator.validate(preCustomer) } returns preCustomer.right()

            useCase.execute(preCustomer, Optional.of(customer)) shouldBeLeft ApplicationError.customerExists()

            verify(exactly = 0) { validator.validate(preCustomer) }
            verify(exactly = 0) { toCustomerMapper.map(preCustomer) }
            verify(exactly = 0) { repository.create(customer) }
        }
    }
})
