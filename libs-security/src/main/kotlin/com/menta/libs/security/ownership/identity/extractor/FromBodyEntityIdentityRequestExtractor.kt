package com.menta.libs.security.ownership.identity.extractor

import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.libs.security.ownership.owner.Owner
import java.util.UUID
import javax.servlet.http.HttpServletRequest

interface FromBodyEntityIdentityRequestExtractor : EntityIdentityRequestExtractor

class DefaultFromBodyEntityIdentityRequestExtractor(
    private val objectMapper: ObjectMapper
) : FromBodyEntityIdentityRequestExtractor {

    override fun extract(request: HttpServletRequest, owner: Owner): UUID? =
        objectMapper.readValue(request.inputStream, Map::class.java)[owner.argumentName]
            ?.let { UUID.fromString(it as String) }
}
