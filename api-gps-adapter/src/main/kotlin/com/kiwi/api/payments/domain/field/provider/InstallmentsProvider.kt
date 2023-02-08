package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.domain.OperationType
import com.kiwi.api.payments.domain.field.Installments
import com.kiwi.api.payments.domain.field.Installments.Financing.AHORA
import com.kiwi.api.payments.domain.field.Installments.Financing.BANK
import org.springframework.stereotype.Component

@Component
class InstallmentsProvider {

    fun provideFrom(installmentCode: String, operationType: OperationType): Installments =
        installmentsMap.getOrDefault(
            installmentCode,
            buildDefaultInstallmentsFor(installmentCode, operationType)
        )

    private fun buildDefaultInstallmentsFor(installmentCode: String, operationType: OperationType) =
        Installments(
            financing = BANK,
            quantity = if (operationType == OperationType.REFUND) "01" else installmentCode
        )

    companion object {
        val installmentsMap = mapOf(
            "13" to Installments(financing = AHORA, quantity = "3"),
            "16" to Installments(financing = AHORA, quantity = "6"),
            "7" to Installments(financing = AHORA, quantity = "12"),
            "8" to Installments(financing = AHORA, quantity = "18"),
            "25" to Installments(financing = AHORA, quantity = "24"),
            "31" to Installments(financing = AHORA, quantity = "30"),
        )
    }
}
