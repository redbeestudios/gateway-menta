package com.menta.api.taxesEntities.adapter.`in`.controller

import arrow.core.Either
import com.menta.api.taxesEntities.adapter.`in`.model.FeeRuleRequest
import com.menta.api.taxesEntities.adapter.`in`.model.mapper.ToFeeRuleMapper
import com.menta.api.taxesEntities.application.port.`in`.AddFeeRulePortIn
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.error.providers.ErrorResponseProvider
import com.menta.api.taxesEntities.shared.utils.evaluate
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import com.menta.api.taxesEntities.shared.utils.logs.benchmark
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/private/taxes-entities")
class FeeRuleController(
    private val addFeeRulePortIn: AddFeeRulePortIn,
    private val toFeeRuleMapper: ToFeeRuleMapper,
    private val errorResponseProvider: ErrorResponseProvider,
) {

    @PostMapping("/customer/{customerId}/feeRule")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMerchantFeeRulesOptions(@PathVariable customerId: UUID, @RequestBody feeRuleRequest: FeeRuleRequest) =
        TaxCustomerController.log.benchmark("add merchant fee rule to a customer") {
            feeRuleRequest
                .toDomain()
                .add(customerId)
                .map { it.toResponse() }
                .asResponseEntity(HttpStatus.CREATED)
        }

    @PostMapping("/merchant/{merchantId}/select")
    @ResponseStatus(HttpStatus.CREATED)
    fun select(@PathVariable merchantId: UUID, @RequestBody feeRules: List<UUID>) =
        TaxCustomerController.log.benchmark("add merchant fee rule to a customer") {
            addFeeRulePortIn
                .select(merchantId, feeRules)
                .map { it.toResponse() }
                .asListResponseEntity(HttpStatus.CREATED)
        }

    private fun FeeRule.add(customerId: UUID) = addFeeRulePortIn.add(customerId, this)

    private fun FeeRuleRequest.toDomain() =
        toFeeRuleMapper.mapFrom(this)
            .log { info("To Fee Rule: {}", it) }

    private fun FeeRule.toResponse() = this.log { info("response: {}", it) }

    private fun List<FeeRule>.toResponse() = this.log { info("response: {}", it) }

    private fun Either<ApplicationError, FeeRule>.asResponseEntity(httpStatus: HttpStatus = HttpStatus.OK) =
        evaluate(rightStatusCode = httpStatus) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    private fun Either<ApplicationError, List<FeeRule>>.asListResponseEntity(httpStatus: HttpStatus = HttpStatus.OK) =
        evaluate(rightStatusCode = httpStatus) { errorResponseProvider.provideFor(this) }
            .log { info("list response entity: {}", it) }
    companion object : CompanionLogger()
}
