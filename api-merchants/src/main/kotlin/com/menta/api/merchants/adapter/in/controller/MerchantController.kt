package com.menta.api.merchants.adapter.`in`.controller

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.merchants.adapter.`in`.model.MerchantRequest
import com.menta.api.merchants.adapter.`in`.model.UpdateRequest
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantResponseMapper
import com.menta.api.merchants.adapter.`in`.model.mapper.ToMerchantUpdater
import com.menta.api.merchants.adapter.`in`.model.mapper.ToPreMerchantMapper
import com.menta.api.merchants.application.port.`in`.CreateMerchantPortIn
import com.menta.api.merchants.application.port.`in`.DeleteMerchantPortIn
import com.menta.api.merchants.application.port.`in`.FindMerchantByFilterPortIn
import com.menta.api.merchants.application.port.`in`.FindMerchantPortIn
import com.menta.api.merchants.application.port.`in`.UpdateMerchantPortIn
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.domain.Pagination
import com.menta.api.merchants.domain.PreMerchant
import com.menta.api.merchants.domain.Status
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import com.menta.api.merchants.shared.utils.logs.benchmark
import com.menta.api.merchants.shared.utils.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/private/merchants")
class MerchantController(
    private val findMerchant: FindMerchantPortIn,
    private val deleteMerchantPortIn: DeleteMerchantPortIn,
    private val toResponseMapper: ToMerchantResponseMapper,
    private val toPreMerchantMapper: ToPreMerchantMapper,
    private val toMerchantUpdater: ToMerchantUpdater,
    private val createMerchantPortIn: CreateMerchantPortIn,
    private val updateMerchantPortIn: UpdateMerchantPortIn,
    private val findMerchantByFilterPortIn: FindMerchantByFilterPortIn
) {

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get merchant by merchantId")
    fun getBy(@PathVariable merchantId: UUID) =
        log.benchmark("find merchant") {
            findMerchant.execute(merchantId)
                .throwIfLeft()
                .toResponse()
        }

    @GetMapping
    fun getBy(
        @RequestParam(required = false) id: UUID?,
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false) customerId: UUID?,
        @RequestParam(required = false) createDate: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) =
        log.benchmark("find merchant") {
            findMerchantByFilterPortIn.execute(
                mapToQuery(id, status, customerId, createDate),
                mapToPagination(page, size)
            ).throwIfLeft()
                .toResponse()
        }

    private fun mapToPagination(page: Int, size: Int) = Pagination(page = page, size = size)

    private fun mapToQuery(
        merchantId: UUID?,
        status: Status?,
        customerId: UUID?,
        createDate: String?
    ) = MerchantQuery(merchantId = merchantId, status = status, customerId = customerId, createDate = createDate)

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create merchant")
    fun create(@RequestBody merchantRequest: MerchantRequest) =
        log.benchmark("create merchant") {
            merchantRequest
                .toDomain()
                .execute()
                .throwIfLeft()
                .toResponse()
        }

    @DeleteMapping("/{merchantId}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "delete merchant")
    fun delete(@PathVariable merchantId: UUID) =
        log.benchmark("delete merchant by id $merchantId") {
            findMerchant.execute(merchantId)
                .flatMap {
                    deleteMerchantPortIn.execute(it)
                }.throwIfLeft()
        }

    @PutMapping("/{merchantId}")
    @ResponseStatus(OK)
    @Operation(summary = "update merchant")
    fun modify(@RequestBody request: MerchantRequest, @PathVariable merchantId: UUID) =
        log.benchmark("update merchant") {
            merchantId
                .findMerchant()
                .map { toMerchantUpdater.applyChanges(it,request) }
                .update()
                .throwIfLeft()
                .toResponse()
        }

    @PatchMapping("/{merchantId}")
    @ResponseStatus(OK)
    @Operation(summary = "update merchant")
    fun modify(@RequestBody request: UpdateRequest, @PathVariable merchantId: UUID) =
        log.benchmark("create merchant") {
            merchantId
                .findMerchant()
                .map { toMerchantUpdater.applyChanges(it,request) }
                .update()
                .throwIfLeft()
                .toResponse()
        }

    private fun UUID.findMerchant() =
        findMerchant.execute(this)

    private fun PreMerchant.execute() =
        createMerchantPortIn.execute(this, findMerchant.findByUnivocity(tax.type, tax.id))

    private fun Either<ApplicationError,Merchant>.update() =
        flatMap { updateMerchantPortIn.execute(it) }

    private fun MerchantRequest.toDomain() =
        toPreMerchantMapper.mapFrom(this)
            .log { info("Created PreMerchant: {}", it) }

    private fun Merchant.toResponse() =
        toResponseMapper.map(this)
            .log { info("response: {}", it) }

    private fun Page<Merchant>.toResponse() =
        toResponseMapper.map(this)
            .log { info("response: {}", it) }

    companion object : CompanionLogger()
}
