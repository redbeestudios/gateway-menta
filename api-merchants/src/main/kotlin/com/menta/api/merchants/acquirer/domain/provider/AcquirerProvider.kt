package com.menta.api.merchants.acquirer.domain.provider

import com.menta.api.merchants.acquirer.domain.Acquirers
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidAcquirer
import com.menta.api.merchants.shared.utils.either.firstOrLeft
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AcquirerProvider(
    private val acquirers: Acquirers
) {

    fun provideFor(acquirer: String) =
        acquirers.operable
            .firstOrLeft(error = invalidAcquirer(acquirer)) { it.id == acquirer }
            .logEither(
                { error("acquirer not found: {}", acquirer) },
                { info("acquirer found: {}", it) }
            )

    companion object : CompanionLogger()
}
