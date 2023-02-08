package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.config.Constants
import org.springframework.stereotype.Component

@Component
class ConstantsProvider(
    private val constants: Constants
) {
    fun provideNII() = constants.networkInternationalIdentifier
    fun providePOSConditionCode() = constants.pointOfServiceConditionCode

    fun provideTerminalOwnerFiid() = constants.terminalOwnerFiid
    fun provideTerminalLogicalNetwork() = constants.terminalLogicalNetwork
    fun provideTerminalTimeOffset() = constants.terminalTimeOffset
    fun provideTerminalId() = constants.terminalId
    fun provideCloseTransactionHour() = constants.closeTransactionHour
    fun provideInfoText() = constants.infoText
    fun getAcquiringInstitutionIdentificationCode() = constants.acquiringInstitutionIdentificationCode
}
