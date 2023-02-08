package com.menta.api.taxesEntities.adapter.`in`.controller

import arrow.core.Either
import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantResponse
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToPreMerchantMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantMapper
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToTaxMerchantResponseMapper
import com.menta.api.taxesEntities.application.port.`in`.CreateTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.`in`.FindTaxMerchantPortIn
import com.menta.api.taxesEntities.application.port.`in`.UpdateTaxMerchantPortIn
import com.menta.api.taxesEntities.domain.PreTaxMerchant
import com.menta.api.taxesEntities.domain.TaxMerchant
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
@RequestMapping("/private/taxes-entities/merchant")
class TaxMerchantController(
    private val findTaxMerchantPortIn: FindTaxMerchantPortIn,
    private val toResponseMapper: ToTaxMerchantResponseMapper,
    private val toTaxMerchantMapper: ToTaxMerchantMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val createTaxMerchantPortIn: CreateTaxMerchantPortIn,
    private val updateTaxMerchantPortIn: UpdateTaxMerchantPortIn,
    private val toPreMerchantMapper: ToPreMerchantMapper,
) {

    @GetMapping("/{merchantId}")
    fun getBy(@PathVariable merchantId: UUID) =
        log.benchmark("find tax merchant") {
            findTaxMerchantPortIn.execute(merchantId)
                .map { it.toResponse() }
                .asResponseEntity()
        }

    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@RequestBody merchantRequest: TaxMerchantRequest) =
        log.benchmark("create tax merchant") {
            merchantRequest
                .toDomain()
                .create()
                .map { it.toResponse() }
                .asResponseEntity(CREATED)
        }

    @PatchMapping("/{merchantId}")
    @ResponseStatus(OK)
    fun update(@PathVariable merchantId: UUID, @RequestBody preTaxMerchantRequest: PreTaxMerchantRequest) =
        log.benchmark("update tax customer") {
            preTaxMerchantRequest
                .toDomain()
                .update(merchantId)
                .map { it.toResponse() }
                .asResponseEntity(OK)
        }

    private fun PreTaxMerchantRequest.toDomain() =
        toPreMerchantMapper.map(this)

    private fun Either<ApplicationError, TaxMerchantResponse>.asResponseEntity(httpStatus: HttpStatus = OK) =
        evaluate(rightStatusCode = httpStatus) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    private fun PreTaxMerchant.update(merchantId: UUID) =
        updateTaxMerchantPortIn.execute(this, merchantId)

    private fun TaxMerchant.create() =
        createTaxMerchantPortIn.execute(this)

    private fun TaxMerchantRequest.toDomain() =
        toTaxMerchantMapper.mapFrom(this)
            .log { info("Created tax Merchant: {}", it) }

    private fun TaxMerchant.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    companion object : CompanionLogger()
}
