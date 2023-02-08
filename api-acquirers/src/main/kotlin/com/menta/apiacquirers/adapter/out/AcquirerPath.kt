package com.menta.apiacquirers.adapter.out

import arrow.core.Either
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForAcquirer
import com.menta.apiacquirers.shared.util.firstOrLeft
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

data class AcquirerPath(
    val name: String,
    val path: String
)

@Component
class AcquirerPathProvider(
    private val acquirersPaths: List<AcquirerPath>
) {

    fun provideBy(acquirer: String): Either<ApplicationError, String> =
        acquirersPaths
            .filter { it.name.uppercase() == acquirer.uppercase() }
            .firstOrLeft(missingPathForAcquirer(acquirer))
            .map { it.path }
            .logEither(
                { error("missing path for $ {}", it) },
                { info("path found: {}", it) }
            )

    companion object : CompanionLogger()
}
