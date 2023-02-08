package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.OperationType.ANNULMENT
import com.menta.api.credibanco.domain.OperationType.ANNULMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PAYMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.domain.OperationType.REFUND
import com.menta.api.credibanco.domain.field.SettlementData
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api-credibanco-adapter.global")
data class SettlementDataProvider(
    val settlementData: SettlementData
) {
    fun provide(operationType: OperationType, settlementDataResponse: String?) =
        when (operationType) {
            PURCHASE, ANNULMENT ->
                with(settlementData) {
                    service + originator + destination + draftCaptureFlag + settlementFlag
                }
            PAYMENT_REVERSE, REFUND -> settlementDataResponse
            ANNULMENT_REVERSE -> null
        }
}
