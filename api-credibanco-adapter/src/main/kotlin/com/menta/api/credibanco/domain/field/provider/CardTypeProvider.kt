package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.OperationType.ANNULMENT
import com.menta.api.credibanco.domain.OperationType.ANNULMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PAYMENT_REVERSE
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.domain.OperationType.REFUND
import com.menta.api.credibanco.domain.field.CardType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api-credibanco-adapter.global")
data class CardTypeProvider(
    val cardType: CardType
) {
    fun provide(operationType: OperationType, terminalTypeResponse: String?) =
        when (operationType) {
            PURCHASE, ANNULMENT ->
                with(cardType) {
                    cardIssuerFiid + logicalNetwork + category + saveAccountIndicator + interchangeResponseCode
                }
            PAYMENT_REVERSE, REFUND -> terminalTypeResponse
            ANNULMENT_REVERSE -> null
        }
}
