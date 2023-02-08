package com.menta.apiacquirers.adapter.out

import arrow.core.Either
import com.menta.apiacquirers.domain.OperationType
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingPathForOperation
import com.menta.apiacquirers.shared.util.firstOrLeft
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

data class OperationPath(
    val name: String,
    val path: String
)

@Component
class OperationPathProvider(
    private val operationsPaths: List<OperationPath>
) {

    fun provideBy(operationType: OperationType): Either<ApplicationError, String> =
        operationsPaths
            .filter { it.name.uppercase() == operationType.name }
            .firstOrLeft(missingPathForOperation(operationType))
            .map { it.path }
            .logEither(
                { error("missing path for $ {}", it) },
                { info("path found: {}", it) }
            )

    companion object : CompanionLogger()
}
