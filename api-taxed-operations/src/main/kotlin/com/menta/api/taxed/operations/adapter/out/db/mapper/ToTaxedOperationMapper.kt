package com.menta.api.taxed.operations.adapter.out.db.mapper

import com.menta.api.taxed.operations.adapter.out.db.entity.Tax
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxCustomer
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxMerchant
import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import com.menta.api.taxed.operations.adapter.out.db.resolver.CreateDatetimeResolver
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import com.menta.api.taxed.operations.domain.TaxDispersion
import com.menta.api.taxed.operations.domain.TaxRule
import org.springframework.stereotype.Component

@Component
class ToTaxedOperationMapper(
    private val createDatetimeResolver: CreateDatetimeResolver
) {

    fun map(paymentTaxedOperation: PaymentTaxedOperation): TaxedOperation =
        with(paymentTaxedOperation) {
            TaxedOperation(
                id = Int.MIN_VALUE,
                paymentId = paymentId,
                terminalId = terminalId,
                merchantId = merchantId,
                customerId = customerId,
                grossAmount = grossAmount,
                currency = currency,
                installments = installments,
                merchantTaxRules = buildTaxesRules(taxes.merchantTaxRules!!),
                customerTaxRules = buildTaxesRules(taxes.customerTaxRules!!),
                taxCustomer = buildTaxCustomer(customerDispersion),
                taxMerchant = buildTaxMerchant(merchantDispersion),
                createDatetime = createDatetimeResolver.provide()
            )
        }

    private fun buildTaxesRules(taxes: List<TaxRule>): Set<Tax> {
        val taxesOperation = mutableSetOf<Tax>()
        taxes.forEach { taxRule -> taxesOperation.add(buildTax(taxRule)) }
        return taxesOperation
    }

    private fun buildTax(taxRule: TaxRule): Tax =
        with(taxRule) {
            Tax(
                id = id,
                name = name,
                description = description,
                percentage = percentage,
                fiscalCondition = fiscalCondition,
                country = country,
                paymentMethod = paymentMethod,
                taxType = taxType
            )
        }

    private fun buildTaxMerchant(merchantDispersion: TaxDispersion): TaxMerchant =
        with(merchantDispersion) {
            TaxMerchant(
                id = Int.MIN_VALUE,
                taxedAmount = taxedAmount,
                iva = iva,
                ganancias = ganancias,
                commission = feeRule.commission,
                grossCommission = grossCommission,
                grossCommissionWithTax = grossCommissionWithTax,
                partialGrossAmount = partialGrossAmount,
                ivaCommission = ivaCommission,
                paymentMethod = feeRule.paymentMethod,
                installments = feeRule.installments,
                term = feeRule.term,
                discount = feeRule.discount,
                nextPaymentDate = nextPaymentDate,
                taxedOperation = null
            )
        }

    private fun buildTaxCustomer(customerDispersion: TaxDispersion): TaxCustomer =
        with(customerDispersion) {
            TaxCustomer(
                id = Int.MIN_VALUE,
                taxedAmount = taxedAmount,
                iva = iva,
                ganancias = ganancias,
                commission = feeRule.commission,
                grossCommission = grossCommission,
                grossCommissionWithTax = grossCommissionWithTax,
                partialGrossAmount = partialGrossAmount,
                ivaCommission = ivaCommission,
                paymentMethod = feeRule.paymentMethod,
                installments = feeRule.installments,
                term = feeRule.term,
                discount = feeRule.discount,
                nextPaymentDate = nextPaymentDate,
                taxedOperation = null
            )
        }
}
