package com.menta.api.customers.customer.adapter.out

import arrow.core.right
import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerId
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Status
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.util.Optional
import java.util.UUID

class CustomerRepositoryTest : FeatureSpec({

    val dbRepository = mockk<CustomerDbRepository>()
    val mongoTemplate = mockk<MongoTemplate>(relaxed = true)
    val repository = CustomerRepository(dbRepository, mongoTemplate)

    beforeEach { clearAllMocks() }

    feature("find by customer id") {

        scenario("customer found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            val customer = aCustomerCreated

            every {
                dbRepository.findByIdAndDeleteDateIsNull(customerId)
            } returns Optional.of(customer)

            repository.findBy(customerId) shouldBe Optional.of(customer)

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(customerId) }
        }

        scenario("customer NOT found") {
            val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            every {
                dbRepository.findByIdAndDeleteDateIsNull(customerId)
            } returns Optional.empty()

            repository.findBy(customerId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(customerId) }
        }
    }

    feature("create") {

        scenario("customer saved") {
            val newCustomer = aCustomerCreated

            every { dbRepository.insert(newCustomer) } returns aCustomerCreated

            repository.create(newCustomer) shouldBe aCustomerCreated

            verify(exactly = 1) { dbRepository.insert(newCustomer) }
        }
    }

    feature("update") {

        scenario("customer updated") {
            val customer = aCustomerCreated

            every { dbRepository.save(customer) } returns aCustomerCreated

            repository.update(customer) shouldBe aCustomerCreated.right()

            verify(exactly = 1) { dbRepository.save(customer) }
        }
    }

    feature("find by tax") {

        scenario("customer found") {
            val customer = aCustomerCreated

            every {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    customer.tax.type,
                    customer.tax.id
                )
            } returns Optional.of(customer)

            repository.findBy(customer.tax.type, customer.tax.id) shouldBe Optional.of(customer)

            verify(exactly = 1) {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    customer.tax.type,
                    customer.tax.id
                )
            }
        }

        scenario("customer NOT found") {

            val customer = aCustomerCreated

            every {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    customer.tax.type,
                    customer.tax.id
                )
            } returns Optional.empty()

            repository.findBy(customer.tax.type, customer.tax.id) shouldBe Optional.empty()

            verify(exactly = 1) {
                dbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(
                    customer.tax.type,
                    customer.tax.id
                )
            }
        }
    }

    feature("find by filter") {

        val pageable = PageRequest.of(0, 10)
        val count = 1L
        val customer = aCustomerCreated
        val result = PageImpl(listOf(customer), pageable, count)

        scenario("customer found filter by id") {

            val customerQuery = CustomerQuery(id = aCustomerId, country = null, status = null, createDate = null)
            val query = Query(Criteria().and("id").`is`(aCustomerId))
            query.limit(pageable.pageSize)

            every { mongoTemplate.count(any(), "customers") } returns count
            every { mongoTemplate.find(any(), Customer::class.java) } returns listOf(customer)

            repository.findBy(customerQuery, pageable) shouldBe result

            verify(exactly = 1) { mongoTemplate.count(any(), "customers") }
            verify(exactly = 1) { mongoTemplate.find(any(), Customer::class.java) }
        }

        scenario("customer found filter by status") {

            val customerQuery = CustomerQuery(id = null, country = null, status = Status.ACTIVE, createDate = null)
            val query = Query(Criteria().and("id").`is`(aCustomerId))
            query.limit(pageable.pageSize)

            every { mongoTemplate.count(any(), "customers") } returns count
            every { mongoTemplate.find(any(), Customer::class.java) } returns listOf(customer)

            repository.findBy(customerQuery, pageable) shouldBe result

            verify(exactly = 1) { mongoTemplate.count(any(), "customers") }
            verify(exactly = 1) { mongoTemplate.find(any(), Customer::class.java) }
        }

        scenario("customer found filter by country") {

            val customerQuery = CustomerQuery(id = null, country = Country.ARG, status = null, createDate = null)
            val query = Query(Criteria().and("id").`is`(aCustomerId))
            query.limit(pageable.pageSize)

            every { mongoTemplate.count(any(), "customers") } returns count
            every { mongoTemplate.find(any(), Customer::class.java) } returns listOf(customer)

            repository.findBy(customerQuery, pageable) shouldBe result

            verify(exactly = 1) { mongoTemplate.count(any(), "customers") }
            verify(exactly = 1) { mongoTemplate.find(any(), Customer::class.java) }
        }

        scenario("customer NOT found") {

            val query = Query(Criteria().and("id").`is`(aCustomerId))
            query.limit(pageable.pageSize)
            val count = 0L
            val resultEmpty = PageImpl(emptyList<Customer>(), pageable, count)
            val customerQuery = CustomerQuery(id = null, country = null, status = Status.ACTIVE, createDate = null)

            every { mongoTemplate.count(any(), "terminals") } returns count
            every { mongoTemplate.find(any(), Customer::class.java) } returns emptyList()

            repository.findBy(customerQuery, pageable) shouldBe resultEmpty

            verify(exactly = 1) { mongoTemplate.count(any(), "customers") }
            verify(exactly = 1) { mongoTemplate.find(any(), Customer::class.java) }
        }
    }
})
