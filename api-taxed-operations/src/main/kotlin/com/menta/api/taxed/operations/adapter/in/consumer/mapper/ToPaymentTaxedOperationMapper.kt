package com.menta.api.taxed.operations.adapter.`in`.consumer.mapper

import com.menta.api.taxed.operations.domain.PaymentTaxCalculation
import com.menta.api.taxed.operations.domain.PaymentTaxedOperation
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToPaymentTaxedOperationMapper {

    fun map(paymentTaxCalculation: PaymentTaxCalculation): PaymentTaxedOperation =
        with(paymentTaxCalculation) {
            PaymentTaxedOperation(
                paymentId = UUID.fromString(createdPayment.id),
                terminalId = createdPayment.data.terminal.id,
                merchantId = createdPayment.data.merchant.id,
                customerId = createdPayment.data.customer.id,
                grossAmount = taxCalculation.grossAmount.toString(),
                currency = createdPayment.data.amount.currency,
                installments = createdPayment.data.installments,
                merchantDispersion = taxCalculation.merchantTaxDispersion,
                customerDispersion = taxCalculation.customerTaxDispersion,
                taxes = taxes
            )
        }
}
