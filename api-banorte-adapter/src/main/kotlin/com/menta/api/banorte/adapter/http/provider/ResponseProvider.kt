package com.menta.api.banorte.adapter.http.provider

import arrow.core.Either
import com.menta.api.banorte.adapter.http.model.HeaderResponse
import com.menta.api.banorte.shared.error.leftIfNull
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.error.model.HeaderExtractionError
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class ResponseProvider {

    fun extractNullable(headers: HttpHeaders, headerResponse: HeaderResponse): String? =
        headers[headerResponse.name]?.get(0)

    fun extract(headers: HttpHeaders, headerResponse: HeaderResponse): Either<ApplicationError, String> =
        extractNullable(headers, headerResponse)
            .leftIfNull(HeaderExtractionError(headerResponse.name))
}
