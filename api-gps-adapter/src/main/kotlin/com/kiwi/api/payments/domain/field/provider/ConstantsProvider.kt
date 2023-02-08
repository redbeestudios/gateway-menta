package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.config.Constants
import org.springframework.stereotype.Component

@Component
class ConstantsProvider(
    private val constants: Constants
) {
    fun provideNII() = constants.networkInternationalIdentifier
    fun providePOSConditionCode() = constants.pointOfServiceConditionCode
    fun provideHandbookVersion() = constants.handbookVersion
    fun provideHardwareVersion() = constants.hardwareVersion
    fun provideAuditNumber() = constants.auditNumber
}
