package com.kiwi.api.reverse.shared.error.providers

import com.kiwi.api.reverse.shared.error.model.ApiError
import com.kiwi.api.reverse.shared.error.model.ApiErrorResponse
import com.kiwi.api.reverse.shared.error.model.ErrorCode
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ErrorResponseProvider(
    private val currentResourceProvider: CurrentResourceProvider,
    private val metadataProvider: ErrorResponseMetadataProvider
) {

    fun provideFor(
        httpStatus: HttpStatus,
        ex: Throwable,
        errorCode: ErrorCode
    ) =
        ApiErrorResponse(
            datetime = OffsetDateTime.now(),
            errors = listOf(
                ApiError(
                    code = errorCode.value,
                    resource = getResource(),
                    message = ex.getDetail(),
                    metadata = getMetadata()
                )
            )
        ).log { debug("error response provided {}", it) }

    private fun getResource() =
        currentResourceProvider.provideUri()

    private fun getMetadata() =
        metadataProvider.provide()

    private fun Throwable.getDetail() =
        "${javaClass.canonicalName}:$message"

    companion object : CompanionLogger()
}
