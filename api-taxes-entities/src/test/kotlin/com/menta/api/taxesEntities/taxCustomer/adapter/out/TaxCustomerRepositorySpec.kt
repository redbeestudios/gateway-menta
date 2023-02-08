package com.menta.api.taxesEntities.taxCustomer.adapter.out

import arrow.core.right
import com.menta.api.taxesEntities.aTaxCustomer
import com.menta.api.taxesEntities.adapter.out.TaxCustomerDbRepository
import com.menta.api.taxesEntities.adapter.out.TaxCustomerRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class TaxCustomerRepositorySpec : FeatureSpec({

    val dbRepository = mockk<TaxCustomerDbRepository>()
    val repository = TaxCustomerRepository(dbRepository)

    beforeEach { clearAllMocks() }

    feature("find tax customer by id") {

        scenario("tax customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val customer = aTaxCustomer()

            every {
                dbRepository.findByCustomerId(customerId)
            } returns Optional.of(customer)

            repository.findBy(customerId) shouldBe Optional.of(customer)

            verify(exactly = 1) { dbRepository.findByCustomerId(customerId) }
        }

        scenario("tax customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            every {
                dbRepository.findByCustomerId(customerId)
            } returns Optional.empty()

            repository.findBy(customerId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByCustomerId(customerId) }
        }
    }

    feature("create") {
        scenario("tax customer create") {
            val customer = aTaxCustomer()

            every { dbRepository.insert(customer) } returns aTaxCustomer()

            repository.create(customer) shouldBe aTaxCustomer().right()
        }
    }

    feature("Update tax customer") {
        scenario("Ok") {
            val customer = aTaxCustomer()

            every { dbRepository.save(customer) } returns aTaxCustomer()

            repository.update(customer) shouldBe aTaxCustomer().right()

            verify(exactly = 1) { dbRepository.save(customer) }
        }
    }
})
