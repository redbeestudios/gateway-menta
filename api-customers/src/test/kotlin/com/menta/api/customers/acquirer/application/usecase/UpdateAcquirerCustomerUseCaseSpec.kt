package com.menta.api.customers.acquirer.application.usecase

import arrow.core.right
import com.menta.api.customers.aPreAcquirerCustomer
import com.menta.api.customers.acquirer.application.port.out.AcquirerCustomerRepositoryOutPort
import com.menta.api.customers.anAcquirerCustomer
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.datetime
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerDoesNotExists
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class UpdateAcquirerCustomerUseCaseSpec : FeatureSpec({

    lateinit var dateProvider: DateProvider

    val repository = mockk<AcquirerCustomerRepositoryOutPort>()

    beforeEach { clearAllMocks() }

    feature("update acquirer customer") {
        dateProvider = mockk()

        val useCase = UpdateAcquirerCustomerUseCase(repository, dateProvider)

        scenario("with pre acquirer customer") {
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomer = anAcquirerCustomer()

            every { repository.update(acquirerCustomer) } returns acquirerCustomer.right()
            every { dateProvider.provide() } returns datetime

            useCase.execute(preAcquirerCustomer, Optional.of(acquirerCustomer)) shouldBeRight acquirerCustomer

            verify(exactly = 1) { dateProvider.provide() }
            verify(exactly = 1) { repository.update(acquirerCustomer) }
        }

        scenario("with acquirer customer not found") {
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomer = anAcquirerCustomer()

            useCase.execute(preAcquirerCustomer, Optional.empty()) shouldBeLeft acquirerCustomerDoesNotExists()

            verify(exactly = 0) { repository.update(acquirerCustomer) }
        }
    }
})
