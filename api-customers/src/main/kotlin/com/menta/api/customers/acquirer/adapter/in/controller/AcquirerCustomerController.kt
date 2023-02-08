package com.menta.api.customers.acquirer.adapter.`in`.controller

import arrow.core.flatMap
import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerRequest
import com.menta.api.customers.acquirer.adapter.`in`.model.mapper.ToAcquirerCustomerResponseMapper
import com.menta.api.customers.acquirer.adapter.`in`.model.mapper.ToPreAcquirerCustomerMapper
import com.menta.api.customers.acquirer.application.port.`in`.CreateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.application.port.`in`.UpdateAcquirerCustomerPortIn
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.acquirer.domain.provider.AcquirerProvider
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import com.menta.api.customers.shared.utils.logs.benchmark
import com.menta.api.customers.shared.utils.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Locale
import java.util.UUID

@RestController
@RequestMapping("/private/customers/{customerId}/acquirers")
class AcquirerCustomerController(
    private val findAcquirerCustomer: FindAcquirerCustomerPortIn,
    private val createAcquirerCustomer: CreateAcquirerCustomerPortIn,
    private val updateAcquirerCustomer: UpdateAcquirerCustomerPortIn,
    private val toResponseMapper: ToAcquirerCustomerResponseMapper,
    private val toPreAcquirerCustomerMapper: ToPreAcquirerCustomerMapper,
    private val acquirerProvider: AcquirerProvider
) {

    @Operation(summary = "Get acquirer by customerId and acquirerId")
    @GetMapping("/{acquirer}")
    @ResponseStatus(OK)
    fun getBy(@PathVariable customerId: UUID, @PathVariable acquirer: String) =
        log.benchmark("find customer $customerId for acquirer $acquirer") {
            getAcquirerBy(acquirer.toUpperCaseWithDefaultLocale()).flatMap {
                doGetBy(customerId, it.id)
            }
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Create acquirer")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody acquirerCustomerRequest: AcquirerCustomerRequest, @PathVariable customerId: UUID) =
        log.benchmark("create acquirer customer") {
            getAcquirerBy(acquirerCustomerRequest.acquirerId.toUpperCaseWithDefaultLocale()).flatMap {
                toDomain(acquirerCustomerRequest, customerId)
                    .create()
            }
                .throwIfLeft()
                .toResponse()
        }

    @Operation(summary = "Update acquirer")
    @PutMapping
    @ResponseStatus(OK)
    fun update(@RequestBody acquirerCustomerRequest: AcquirerCustomerRequest, @PathVariable customerId: UUID) =
        log.benchmark("update acquirer customer") {
            getAcquirerBy(acquirerCustomerRequest.acquirerId.toUpperCaseWithDefaultLocale()).flatMap {
                toDomain(acquirerCustomerRequest, customerId)
                    .update()
            }
                .throwIfLeft()
                .toResponse()
        }

    private fun String.toUpperCaseWithDefaultLocale() =
        this.uppercase(Locale.getDefault())

    private fun doGetBy(customerId: UUID, acquirer: String) =
        findAcquirerCustomer.execute(customerId, acquirer)
            .logRight { info("acquirer customer found: {}", it) }

    private fun getAcquirerBy(acquirer: String) =
        acquirerProvider.provideFor(acquirer)
            .logRight { info("acquirer found: {}", it) }

    private fun toDomain(acquirerCustomerRequest: AcquirerCustomerRequest, customerId: UUID) =
        toPreAcquirerCustomerMapper.map(acquirerCustomerRequest, customerId)
            .log { info("Created acquirer customer: {}", it) }

    private fun PreAcquirerCustomer.create() =
        createAcquirerCustomer.execute(this, findAcquirerCustomer.find(customerId, acquirerId))

    private fun PreAcquirerCustomer.update() =
        updateAcquirerCustomer.execute(this, findAcquirerCustomer.find(customerId, acquirerId))

    private fun AcquirerCustomer.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    companion object : CompanionLogger()
}
