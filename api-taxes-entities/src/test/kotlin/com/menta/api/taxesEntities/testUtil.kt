package com.menta.api.taxesEntities

import com.menta.api.taxesEntities.TestConstants.Companion.CBU_OR_CVU
import com.menta.api.taxesEntities.TestConstants.Companion.COMMISSION
import com.menta.api.taxesEntities.TestConstants.Companion.CUSTOMER_ID
import com.menta.api.taxesEntities.TestConstants.Companion.INSTALLMENTS
import com.menta.api.taxesEntities.TestConstants.Companion.MERCHANT_ID
import com.menta.api.taxesEntities.TestConstants.Companion.TAX_CUSTOMER_ID
import com.menta.api.taxesEntities.TestConstants.Companion.TAX_MERCHANT_ID
import com.menta.api.taxesEntities.TestConstants.Companion.TERM
import com.menta.api.taxesEntities.adapter.`in`.model.FeeRuleRequest
import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.PreTaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxCustomerResponse
import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantRequest
import com.menta.api.taxesEntities.adapter.`in`.model.TaxMerchantResponse
import com.menta.api.taxesEntities.domain.Country.ARG
import com.menta.api.taxesEntities.domain.FeeRule
import com.menta.api.taxesEntities.domain.FiscalCondition.RESPONSABLE_INSCRIPTO
import com.menta.api.taxesEntities.domain.PaymentMethod.CREDIT
import com.menta.api.taxesEntities.domain.PreTaxCustomer
import com.menta.api.taxesEntities.domain.PreTaxMerchant
import com.menta.api.taxesEntities.domain.SettlementCondition
import com.menta.api.taxesEntities.domain.TaxCustomer
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApiErrorResponse
import com.menta.api.taxesEntities.shared.error.model.ApiErrorResponse.ApiError
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

val anApiErrorResponse =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiError(
                code = "a code",
                resource = "a resource",
                message = "a message",
                metadata = mapOf("a_key" to "a value")
            )
        )
    )

fun aTaxMerchant() =
    TaxMerchant(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = 40,
                installments = 3,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = "",
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aPreTaxCustomer() =
    PreTaxCustomer(
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = TERM,
                installments = INSTALLMENTS,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = CBU_OR_CVU,
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        ),
        taxCondition = RESPONSABLE_INSCRIPTO,
    )

fun aPreTaxMerchant() =
    PreTaxMerchant(
        customerId = UUID.fromString(CUSTOMER_ID),
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = TERM,
                installments = INSTALLMENTS,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = CBU_OR_CVU,
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        ),
        taxCondition = RESPONSABLE_INSCRIPTO,
    )

fun aTaxMerchantRequest() =
    TaxMerchantRequest(
        merchantId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = "",
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aTaxMerchantResponse() =
    TaxMerchantResponse(
        id = UUID.fromString(TAX_MERCHANT_ID),
        merchantId = UUID.fromString(MERCHANT_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = 40,
                installments = 3,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = "",
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aTaxCustomer() =
    TaxCustomer(
        id = UUID.fromString(TAX_CUSTOMER_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = TERM,
                installments = INSTALLMENTS,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = CBU_OR_CVU,
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        ),
        merchantFeeRulesOptions = aTaxMerchant().feeRules
    )

fun aPreTaxCustomerRequest() =
    PreTaxCustomerRequest(
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = TERM,
                installments = INSTALLMENTS,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = CBU_OR_CVU,
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aPreTaxMerchantRequest() =
    PreTaxMerchantRequest(
        customerId = UUID.fromString(CUSTOMER_ID),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = TERM,
                discount = BigDecimal.ZERO,
                installments = INSTALLMENTS,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = CBU_OR_CVU,
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aTaxCustomerRequest() =
    TaxCustomerRequest(
        customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = 40,
                installments = 3,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = "",
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        ),
        merchantFeeRulesOptions = aTaxMerchant().feeRules
    )

fun anApplicationErrorResponse(applicationError: ApplicationError) =
    with(applicationError) {
        ApiErrorResponse(
            datetime = OffsetDateTime.now(),
            errors = listOf(
                ApiError(
                    code = code,
                    resource = "a resource",
                    message = message ?: "",
                    metadata = mapOf("a_key" to "a value")
                )
            )
        )
    }

fun aTaxCustomerResponse() =
    TaxCustomerResponse(
        id = UUID.fromString(TAX_CUSTOMER_ID),
        customerId = UUID.fromString(CUSTOMER_ID),
        taxCondition = RESPONSABLE_INSCRIPTO,
        country = ARG,
        feeRules = listOf(
            FeeRule(
                id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
                paymentMethod = CREDIT,
                term = 40,
                installments = 3,
                commission = COMMISSION.toBigDecimal(),
                mentaCommission = COMMISSION.toBigDecimal(),
                discount = BigDecimal.ZERO
            )
        ),
        settlementCondition = SettlementCondition(
            cbuOrCvu = "",
            accountType = SettlementCondition.AccountType(
                id = "01",
                type = "CAJA DE AHORRO"
            )
        )
    )

fun aFeeRuleRequest() =
    FeeRuleRequest(
        paymentMethod = CREDIT,
        term = 40,
        installments = 3,
        commission = COMMISSION.toBigDecimal(),
        mentaCommission = COMMISSION.toBigDecimal(),
        discount = BigDecimal.ZERO
    )

fun aFeeRule() =
    FeeRule(
        id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389c"),
        paymentMethod = CREDIT,
        term = 40,
        installments = 1,
        commission = COMMISSION.toBigDecimal(),
        mentaCommission = COMMISSION.toBigDecimal(),
        discount = BigDecimal.ZERO
    )
