package com.menta.api.credibanco.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api-credibanco-adapter.global.constants")
data class Constants(
    val networkInternationalIdentifier: String,
    val pointOfServiceConditionCode: String,
    val terminalOwnerFiid: String,
    val terminalLogicalNetwork: String,
    val terminalTimeOffset: String,
    val terminalId: String,
    val closeTransactionHour: Int,
    val infoText: String,
    val acquiringInstitutionIdentificationCode: String
)
