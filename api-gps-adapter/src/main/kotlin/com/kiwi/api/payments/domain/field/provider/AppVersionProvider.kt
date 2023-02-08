package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.domain.field.AppVersion
import org.springframework.stereotype.Component

@Component
class AppVersionProvider(
    private val constantsProvider: ConstantsProvider
) {

    fun provide(softwareVersion: String) =
        AppVersion(
            // TODO: Completar el campo en el yml
            hardware = constantsProvider.provideHardwareVersion(),
            softwareVersion = softwareVersion,
            handbookVersion = constantsProvider.provideHandbookVersion()
        )
}
