package com.menta.api.merchants.acquirer.adapter.`in`.controller

import arrow.core.flatMap
import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantRequest
import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantUpdateRequest
import com.menta.api.merchants.acquirer.adapter.`in`.model.mapper.ToAcquirerMerchantResponseMapper
import com.menta.api.merchants.acquirer.adapter.`in`.model.mapper.ToPreAcquirerMerchantMapper
import com.menta.api.merchants.acquirer.application.port.`in`.CreateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.`in`.FindAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.application.port.`in`.UpdateAcquirerMerchantPortIn
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.acquirer.domain.provider.AcquirerProvider
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import com.menta.api.merchants.shared.utils.logs.benchmark
import com.menta.api.merchants.shared.utils.throwIfLeft
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus.CREATED
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
@RequestMapping("/private/merchants/{id}/acquirers")
class AcquirerMerchantController(
    private val findAcquirerMerchant: FindAcquirerMerchantPortIn,
    private val createAcquirerMerchant: CreateAcquirerMerchantPortIn,
    private val updateAcquirerMerchant: UpdateAcquirerMerchantPortIn,
    private val toResponseMapper: ToAcquirerMerchantResponseMapper,
    private val toPreAcquirerMerchantMapper: ToPreAcquirerMerchantMapper,
    private val acquirerProvider: AcquirerProvider
) {

    @GetMapping("/{acquirer}")
    @Operation(summary = "Get acquirer merchant by merchantId & acquirerId")
    fun getBy(@PathVariable acquirer: String, @PathVariable id: UUID) =
        log.benchmark("find merchant: $id for acquirer: $acquirer") {
            getAcquirerBy(acquirer.uppercase(Locale.getDefault())).flatMap {
                doGetBy(it.id, id)
            }.throwIfLeft()
                .toResponse()
        }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create acquirer merchant")
    fun create(@RequestBody acquirerMerchantRequest: AcquirerMerchantRequest, @PathVariable id: UUID) =
        log.benchmark("create acquirer merchant") {
            getAcquirerBy(acquirerMerchantRequest.acquirerId.uppercase(Locale.getDefault())).flatMap {
                toDomain(acquirerMerchantRequest, id)
                    .create()
            }.throwIfLeft()
                .toResponse()
        }

    @PutMapping
    @ResponseStatus(OK)
    @Operation(summary = "Update acquirer merchant")
    fun update(@RequestBody acquirerMerchantRequest: AcquirerMerchantUpdateRequest, @PathVariable id: UUID) =
        log.benchmark("update acquirer merchant") {
            getAcquirerBy(acquirerMerchantRequest.acquirerId.uppercase(Locale.getDefault())).flatMap {
                toDomain(acquirerMerchantRequest, id)
                    .update()
            }.throwIfLeft()
                .toResponse()
        }

    private fun getAcquirerBy(acquirer: String) =
        acquirerProvider.provideFor(acquirer)
            .logRight { info("acquirer found: {}", it) }

    private fun toDomain(acquirerMerchantRequest: AcquirerMerchantRequest, merchantId: UUID) =
        toPreAcquirerMerchantMapper.map(acquirerMerchantRequest, merchantId)
            .log { info("Created acquirer merchant: {}", it) }

    private fun toDomain(acquirerMerchantUpdateRequest: AcquirerMerchantUpdateRequest, merchantId: UUID) =
        toPreAcquirerMerchantMapper.map(acquirerMerchantUpdateRequest, merchantId)
            .log { info("Created acquirer merchant: {}", it) }

    private fun PreAcquirerMerchant.create() =
        createAcquirerMerchant.execute(this, findAcquirerMerchant.find(merchantId, acquirerId))

    private fun PreAcquirerMerchant.update() =
        updateAcquirerMerchant.execute(this, findAcquirerMerchant.find(merchantId, acquirerId))

    private fun doGetBy(acquirer: String, id: UUID) =
        findAcquirerMerchant.execute(acquirer, id)
            .logRight { info("acquirer merchant found: {}", it) }

    private fun AcquirerMerchant.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    companion object : CompanionLogger()
}
