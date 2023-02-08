package com.menta.api.feenicia.shared.error.providers

import com.menta.api.feenicia.shared.error.model.ApiErrorResponse
import com.menta.api.feenicia.shared.error.model.ApiErrorResponse.ApiError
import com.menta.api.feenicia.shared.error.model.ApplicationError
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ErrorResponseProvider(
    private val currentResourceProvider: CurrentResourceProvider,
    private val metadataProvider: ErrorResponseMetadataProvider
) {

    fun provideFor(
        error: ApplicationError,
        ex: Throwable? = null
    ) =
        ApiErrorResponse(
            datetime = OffsetDateTime.now(),
            errors = listOf(
                ApiError(
                    code = error.code,
                    resource = getResource(),
                    message = ex?.getDetail() ?: error.message,
                    metadata = getMetadata()
                )
            )
        ).log { debug("error response provided {}", it) }

    private fun getResource() =
        currentResourceProvider.provideUri()

    private fun getMetadata() =
        metadataProvider.provide()

    private fun Throwable.getDetail() = message

    companion object : CompanionLogger()
}
