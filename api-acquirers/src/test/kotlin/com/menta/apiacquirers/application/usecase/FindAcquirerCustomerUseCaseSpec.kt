package com.menta.apiacquirers.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.apiacquirers.aAcquirerCustomer
import com.menta.apiacquirers.application.port.out.FindAcquirerCustomerPortOut
import com.menta.apiacquirers.customerId
import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.domain.provider.AcquirerProvider
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindAcquirerCustomerUseCaseSpec : FeatureSpec({

    val repository = mockk<FindAcquirerCustomerPortOut>()
    val provider = mockk<AcquirerProvider>()
    val useCase = FindAcquirerCustomerUseCase(repository, provider)

    beforeEach { clearAllMocks() }

    feature("find acquirer customer") {
        val customerId = customerId
        val acquirerId = "GPS"
        val country = "ARG"
        val acquirer = OperableAcquirers.Acquirer(name = "GPS", country = "ARG")

        scenario("acquirer customer found") {
            val acquirerCustomer = aAcquirerCustomer()

            every { provider.provideBy(country) } returns acquirer.right()
            every { repository.findBy(customerId, acquirer.name) } returns acquirerCustomer.right()

            useCase.execute(customerId, country) shouldBeRight acquirerCustomer

            verify(exactly = 1) { provider.provideBy(country) }
            verify(exactly = 1) { repository.findBy(customerId, acquirerId) }
        }
        scenario("acquirer customer NOT found") {
            val error = acquirerCustomerNotFound(customerId, acquirer.name)

            every { provider.provideBy(country) } returns acquirer.right()
            every { repository.findBy(customerId, acquirer.name) } returns error.left()

            useCase.execute(customerId, country) shouldBeLeft error

            verify(exactly = 1) { provider.provideBy(country) }
            verify(exactly = 1) { repository.findBy(customerId, acquirer.name) }
        }
    }
})
