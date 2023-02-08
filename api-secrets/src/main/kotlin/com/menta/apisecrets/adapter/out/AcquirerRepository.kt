package com.menta.apisecrets.adapter.out

import arrow.core.Either
import com.menta.apisecrets.adapter.out.model.OperableAcquirer
import com.menta.apisecrets.application.port.out.AcquirerRepositoryOutPort
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Country
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apisecrets.shared.util.firstOrLeft
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AcquirerRepository(
    private val operableAcquirer: OperableAcquirer
) : AcquirerRepositoryOutPort {

    override fun findBy(country: Country): Either<ApplicationError, Acquirer> =
        operableAcquirer.acquirers
            .filter { it.country.contains(country) }
            .map { it.name }
            .firstOrLeft(acquirerNotFound())
            .logEither(
                { error("Error searching for acquirer with country: {}", country) },
                { info("Acquirer found: {}", it) }
            )

    companion object : CompanionLogger()
}
