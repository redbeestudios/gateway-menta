package com.menta.api.customers.customer.adapter.`in`.controller

import arrow.core.flatMap
import com.menta.api.customers.customer.adapter.`in`.model.CustomerRequest
import com.menta.api.customers.customer.adapter.`in`.model.UpdateRequest
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToCustomerResponseMapper
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToCustomerUpdater
import com.menta.api.customers.customer.adapter.`in`.model.mapper.ToPreCustomerMapper
import com.menta.api.customers.customer.application.port.`in`.CreateCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.DeleteCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.FindCustomerByFilterPortIn
import com.menta.api.customers.customer.application.port.`in`.FindCustomerPortIn
import com.menta.api.customers.customer.application.port.`in`.UpdateCustomerPortIn
import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.CustomerQuery
import com.menta.api.customers.customer.domain.Pagination
import com.menta.api.customers.customer.domain.PreCustomer
import com.menta.api.customers.customer.domain.Status
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import com.menta.api.customers.shared.utils.logs.benchmark
import com.menta.api.customers.shared.utils.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/private/customers")
class CustomerController(
    private val createCustomer: CreateCustomerPortIn,
    private val deleteCustomer: DeleteCustomerPortIn,
    private val updateCustomer: UpdateCustomerPortIn,
    private val findCustomer: FindCustomerPortIn,
    private val findCustomerByFilter: FindCustomerByFilterPortIn,
    private val responseMapper: ToCustomerResponseMapper,
    private val customerMapper: ToPreCustomerMapper,
    private val customerUpdater: ToCustomerUpdater
) {

    @Operation(summary = "Get customer by customerId")
    @GetMapping("/{customerId}")
    fun getBy(@PathVariable customerId: UUID) =
        log.benchmark("find customer by id $customerId") {
            findCustomer.execute(customerId)
                .throwIfLeft()
                .toResponse()
        }

    @GetMapping
    fun getByFilterBy(
        @RequestParam(required = false) id: UUID?,
        @RequestParam(required = false) country: Country?,
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false) createDate: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) =
        log.benchmark("find customers") {
            findCustomerByFilter
                .execute(mapToQuery(id, country, status, createDate), mapToPagination(page, size))
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Create customer")
    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@RequestBody customerRequest: CustomerRequest) =
        log.benchmark("create customer") {
            customerRequest
                .toDomain()
                .save()
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Delete customer by customerId")
    @DeleteMapping("/{customerId}")
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable customerId: UUID) =
        log.benchmark("delete customer by id $customerId") {
            findCustomerById(customerId)
                .flatMap {
                    deleteCustomer.execute(it)
                }.throwIfLeft()
        }

    @Operation(summary = "Update customer")
    @PutMapping("/{customerId}")
    @ResponseStatus(OK)
    fun update(@PathVariable customerId: UUID, @RequestBody request: UpdateRequest) =
        log.benchmark("update customer: $customerId") {
            findCustomerById(customerId)
                .flatMap {
                    it.applyChanges(request)
                        .update()
                }.throwIfLeft()
                .toResponse()
        }

    private fun findCustomerById(id: UUID) =
        findCustomer.execute(id)

    private fun Customer.toResponse() =
        responseMapper.map(this)
            .log { info("response: {}", it) }

    private fun Page<Customer>.toResponse() =
        responseMapper.map(this)
            .log { info("response: {}", it) }

    private fun PreCustomer.save() =
        createCustomer.execute(this, findCustomer.findByUnivocity(tax.type, tax.id))

    private fun Customer.applyChanges(changes: UpdateRequest) =
        customerUpdater.applyChanges(this, changes)

    private fun Customer.update() =
        updateCustomer.execute(this)

    private fun CustomerRequest.toDomain() =
        customerMapper.map(this)
            .log { info("Created PreCustomer: {}", it) }

    private fun mapToQuery(
        id: UUID?,
        country: Country?,
        status: Status?,
        createDate: String?
    ) = CustomerQuery(id = id, country = country, status = status, createDate = createDate)

    private fun mapToPagination(page: Int, size: Int) = Pagination(page = page, size = size)

    companion object : CompanionLogger()
}
