package com.menta.api.customers.acquirer.domain.provider

import com.menta.api.customers.acquirer.domain.Acquirers
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidAcquirer
import com.menta.api.customers.shared.utils.either.firstOrLeft
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AcquirerProvider(
    private val acquirers: Acquirers
) {

    fun provideFor(acquirerId: String) =
        acquirers.operable
            .firstOrLeft(error = invalidAcquirer(acquirerId)) { it.id == acquirerId }
            .logEither(
                { error("acquirer not found: {}", acquirerId) },
                { info("acquirer found: {}", it) }
            )

    companion object : CompanionLogger()
}
