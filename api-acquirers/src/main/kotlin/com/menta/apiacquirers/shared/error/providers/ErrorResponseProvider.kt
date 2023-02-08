package com.menta.apiacquirers.shared.error.providers

import com.menta.apiacquirers.shared.error.model.ApiError
import com.menta.apiacquirers.shared.error.model.ApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ErrorResponseProvider(
    private val currentResourceProvider: CurrentResourceProvider,
    private val metadataProvider: ErrorResponseMetadataProvider
) {

    fun provideFor(error: ApplicationError) =
        ApiErrorResponse(
            datetime = OffsetDateTime.now(),
            errors = listOf(
                ApiError(
                    code = error.code,
                    resource = getResource(),
                    message = error.message ?: error.origin?.message ?: "",
                    metadata = getMetadata()
                )
            )
        ).log { debug("error response provided {}", it) }

    private fun getResource() =
        currentResourceProvider.provideUri()

    private fun getMetadata() =
        metadataProvider.provide()

    companion object : CompanionLogger()
}
