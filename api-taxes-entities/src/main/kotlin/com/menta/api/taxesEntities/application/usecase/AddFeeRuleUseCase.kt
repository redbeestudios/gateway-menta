package com.menta.api.taxesEntities.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.taxesEntities.application.port.`in`.AddFeeRulePortIn
import com.menta.api.taxesEntities.application.port.out.TaxCustomerRepositoryOutPort
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.feeRuleNotFound
import com.menta.api.taxesEntities.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AddFeeRuleUseCase(
    private val taxCustomerRepositoryOutPort: TaxCustomerRepositoryOutPort,
    private val taxMerchantRepositoryOutPort: TaxMerchantRepositoryOutPort
) : AddFeeRulePortIn {

    override fun add(customerId: UUID, feeRule: FeeRule): Either<ApplicationError, FeeRule> {
        taxCustomerRepositoryOutPort
            .findBy(customerId)
            .get()
            .let {
                it.copy(merchantFeeRulesOptions = (it.merchantFeeRulesOptions ?: listOf()) + feeRule)
            }.also {
                taxCustomerRepositoryOutPort.update(it)
            }

        return feeRule.right()
    }

    override fun select(merchantId: UUID, feeRuleIds: List<UUID>): Either<ApplicationError, List<FeeRule>> =
        findMerchant(merchantId)
            .let { taxMerchant ->
                findCustomerMerchantFeeRulesOptions(taxMerchant.customerId)
                    .validateFeeRuleIds(feeRuleIds)
                    .flatMap { feeRules ->
                        feeRules
                            .filter { feeRule ->
                                feeRule.id in feeRuleIds && taxMerchant.feeRules?.none { merchantFeeRule ->
                                    feeRule.paymentMethod == merchantFeeRule.paymentMethod && feeRule.installments == merchantFeeRule.installments
                                } ?: true
                            }
                            .also {
                                taxMerchantRepositoryOutPort.update(
                                    taxMerchant.copy(feeRules = feeRules)
                                )
                            }
                            .right()
                    }
            }

    private fun findMerchant(merchantId: UUID) =
        taxMerchantRepositoryOutPort
            .findBy(merchantId)
            .get()

    private fun findCustomerMerchantFeeRulesOptions(customerId: UUID) =
        taxCustomerRepositoryOutPort
            .findBy(customerId)
            .get()
            .merchantFeeRulesOptions ?: listOf()

    private fun List<FeeRule>.validateFeeRuleIds(feeRuleIds: List<UUID>) =
        feeRuleIds.firstOrNull { id -> id !in map { it.id } }
            ?.let { id ->
                feeRuleNotFound(id)
                    .left()
                    .log { error("merchant fee rule not found") }

            } ?: right()

    companion object : CompanionLogger()
}
