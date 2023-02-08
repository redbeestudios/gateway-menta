package com.menta.api.customers.customer.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.serverError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import com.menta.api.customers.shared.utils.logs.benchmark
import com.menta.api.customers.shared.utils.logs.exception
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Optional
import java.util.UUID

@Component
class CustomerRepository(
    private val customerDbRepository: CustomerDbRepository,
    private val mongoTemplate: MongoTemplate
) : CustomerRepositoryOutPort {

    override fun findBy(customerId: UUID): Optional<Customer> =
        log.benchmark("find customer") {
            customerDbRepository.findByIdAndDeleteDateIsNull(customerId)
        }.log { info("customer found: {}", it) }

    override fun findBy(taxType: String, taxId: String): Optional<Customer> =
        log.benchmark("find customer by tax") {
            customerDbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(taxType, taxId)
        }.log { info("customer found: {}", it) }

    override fun findBy(customerQuery: CustomerQuery, pageable: Pageable): Page<Customer> =
        log.benchmark("find customer entity by filter") {
            getCriteria(customerQuery)
                .toQuery()
                .getPage(pageable, Customer::class.java)
        }

    override fun create(customer: Customer): Customer =
        log.benchmark("customer create") {
            customerDbRepository.insert(customer)
        }.log { info("customer created: {}", it) }

    override fun update(customer: Customer): Either<ApplicationError, Customer> =
        try {
            log.benchmark("update customer") {
                customerDbRepository.save(customer).right()
            }.log { info("customer updated: {}", it) }
        } catch (ex: Exception) {
            serverError(ex).left()
                .log { exception(ex) }
        }

    private fun getCriteria(customerQuery: CustomerQuery) =
        with(customerQuery) {
            Criteria()
                .apply { and("deleteDate").`is`(null) }
                .apply { id?.let { and("id").`is`(it) } }
                .apply { country?.let { and("country").`is`(it.name) } }
                .apply { status?.let { and("status").`is`(it.name) } }
                .apply { createDate?.let {
                    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd H:m:s")
                    val fromDate = dateFormat.parse(it.plus(" 00:00:00"))
                    val toDate = dateFormat.parse(it.plus(" 23:59:59"))

                    andOperator(
                        Criteria.where("createDate").gte(fromDate),
                        Criteria.where("createDate").lte(toDate),
                    )
                } }
        }

    private fun Criteria.toQuery() = Query(this)

    private fun <T> Query.getPage(pageable: Pageable, entityClass: Class<T>): Page<T> = let { query ->
        (
            mongoTemplate.count(query, "customers")
                to
                    query
                        .with(pageable)
                        .let { mongoTemplate.find(query, entityClass) }
            )
            .let { result ->
                PageImpl(result.second, pageable, result.first)
            }
    }

    companion object : CompanionLogger()
}
