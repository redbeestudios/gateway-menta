package com.menta.api.customers.shared.error.providers

import com.menta.api.customers.shared.error.model.ApiErrorResponse
import com.menta.api.customers.shared.error.model.ApiErrorResponse.ApiError
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.utils.logs.CompanionLogger
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
                    resource = getResource(),
                    message = error.message,
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
