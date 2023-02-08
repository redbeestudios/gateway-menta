package com.menta.api.customers.acquirer.adapter.out

import arrow.core.right
import com.menta.api.customers.anAcquirerCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class AcquirerCustomerSafeRepositorySpec : FeatureSpec({

    val dbRepository = mockk<AcquirerCustomerDbRepository>()
    val repository = AcquirerCustomerRepository(dbRepository)

    beforeEach { clearAllMocks() }

    feature("find by customer and acquirer id") {

        scenario("acquirer customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "an acquirer id"
            val acquirerCustomer = anAcquirerCustomer()

            every {
                dbRepository.findByCustomerIdAndAcquirerId(customerId, acquirerId)
            } returns Optional.of(acquirerCustomer)

            repository.findBy(customerId, acquirerId) shouldBe Optional.of(acquirerCustomer)

            verify(exactly = 1) { dbRepository.findByCustomerIdAndAcquirerId(customerId, acquirerId) }
        }

        scenario("acquirer customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val acquirerId = "an acquirer id"

            every {
                dbRepository.findByCustomerIdAndAcquirerId(customerId, acquirerId)
            } returns Optional.empty()

            repository.findBy(customerId, acquirerId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByCustomerIdAndAcquirerId(customerId, acquirerId) }
        }
    }

    feature("create") {
        scenario("acquirer customer saved") {
            val newAcquirerCustomer = anAcquirerCustomer()

            every { dbRepository.insert(newAcquirerCustomer) } returns anAcquirerCustomer()

            repository.create(newAcquirerCustomer) shouldBe anAcquirerCustomer()

            verify(exactly = 1) { dbRepository.insert(newAcquirerCustomer) }
        }
    }

    feature("update") {
        scenario("acquirer customer updated") {
            val acquirerCustomer = anAcquirerCustomer()

            every { dbRepository.save(acquirerCustomer) } returns anAcquirerCustomer()

            repository.update(acquirerCustomer) shouldBe anAcquirerCustomer().right()

            verify(exactly = 1) { dbRepository.save(acquirerCustomer) }
        }
    }
})
