package com.menta.api.credibanco.domain.field.provider

import com.menta.api.credibanco.domain.field.TerminalData
import org.springframework.stereotype.Component

@Component
class TerminalDataProvider(
    private val constantsProvider: ConstantsProvider
) {

    fun provide() =
        TerminalData(
            ownerFiid = constantsProvider.provideTerminalOwnerFiid(),
            logicalNetwork = constantsProvider.provideTerminalLogicalNetwork(),
            timeOffset = constantsProvider.provideTerminalTimeOffset(),
            terminalId = constantsProvider.provideTerminalId()
        )
}
