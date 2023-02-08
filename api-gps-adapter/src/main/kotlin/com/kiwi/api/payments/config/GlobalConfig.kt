package com.kiwi.api.payments.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "api-gp-adapter.global.constants")
data class Constants(
    val networkInternationalIdentifier: String,
    val pointOfServiceConditionCode: String,
    val handbookVersion: String,
    val hardwareVersion: String,
    val auditNumber: String,
)
