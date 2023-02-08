package com.menta.apiacquirers.domain.provider

import arrow.core.Either
import com.menta.apiacquirers.domain.OperableAcquirers
import com.menta.apiacquirers.domain.OperableAcquirers.Acquirer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apiacquirers.shared.util.firstOrLeft
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AcquirerProvider(
    private val operableAcquirers: OperableAcquirers
) {

    fun provideBy(country: String): Either<ApplicationError, Acquirer> =
        operableAcquirers.acquirers
            .filter { it.country == country }
            .firstOrLeft(acquirerNotFound(country))
            .logEither(
                { error("acquirer not found for country: {}", it) },
                { info("acquirer found: {}", it) }
            )

    companion object : CompanionLogger()
}
