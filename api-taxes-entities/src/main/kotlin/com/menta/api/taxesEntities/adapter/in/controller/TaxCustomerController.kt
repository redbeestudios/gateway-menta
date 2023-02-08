package com.menta.api.taxesEntities.adapter.`in`.controller

import arrow.core.Either
import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerResponse
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreTaxCustomerMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxCustomerResponseMapper
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.`in`.FindTaxCustomerPortIn
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxCustomerPorIn
import com.menta.api.taxesEntities.domain.PreTaxCustomer
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.error.providers.ErrorResponseProvider
import com.menta.api.taxesEntities.shared.utils.evaluate
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import com.menta.api.taxesEntities.shared.utils.logs.benchmark
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/private/taxes-entities/customer")
class TaxCustomerController(
    private val findTaxCustomerPortIn: FindTaxCustomerPortIn,
    private val toResponseMapper: ToTaxCustomerResponseMapper,
    private val toTaxCustomerMapper: ToTaxCustomerMapper,
    private val toPreTaxCustomerMapper: ToPreTaxCustomerMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val createTaxCustomerPortIn: CreateTaxCustomerPortIn,
    private val updateTaxCustomerPorIn: UpdateTaxCustomerPorIn
) {

    @GetMapping("/{customerId}")
    fun getBy(@PathVariable customerId: UUID) =
        log.benchmark("find tax customer") {
            findTaxCustomerPortIn.execute(customerId)
                .map { it.toResponse() }
                .asResponseEntity()
        }

    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@RequestBody customerRequest: TaxCustomerRequest) =
        log.benchmark("create tax custromer") {
            customerRequest
                .toDomain()
                .create()
                .map { it.toResponse() }
                .asResponseEntity(CREATED)
        }

    @PatchMapping("/{customerId}")
    @ResponseStatus(OK)
    fun update(@PathVariable customerId: UUID, @RequestBody preTaxCustomer: PreTaxCustomerRequest) =
        log.benchmark("update tax customer") {
            preTaxCustomer
                .toDomain()
                .update(customerId)
                .map { it.toResponse() }
                .asResponseEntity(OK)
        }

    private fun Either<ApplicationError, TaxCustomerResponse>.asResponseEntity(httpStatus: HttpStatus = OK) =
        evaluate(rightStatusCode = httpStatus) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    private fun TaxCustomer.create() =
        createTaxCustomerPortIn.execute(this)

    private fun PreTaxCustomer.update(customerId: UUID) =
        updateTaxCustomerPorIn.execute(this, customerId)

    private fun TaxCustomerRequest.toDomain() =
        toTaxCustomerMapper.mapFrom(this)
            .log { info("Created tax Customer: {}", it) }

    private fun PreTaxCustomerRequest.toDomain() =
        toPreTaxCustomerMapper.map(this)
            .log { info("To Pre tax Customer: {}", it) }

    private fun TaxCustomer.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    companion object : CompanionLogger()
}
