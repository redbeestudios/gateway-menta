package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.Owner
import java.util.UUID
import javax.servlet.http.HttpServletRequest

interface FromQueryParamEntityIdentityRequestExtractor : EntityIdentityRequestExtractor

class DefaultFromQueryParamEntityIdentityRequestExtractor : FromQueryParamEntityIdentityRequestExtractor {
    override fun extract(request: HttpServletRequest, owner: Owner): UUID? =
        request.getParameterValues(owner.argumentName)
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                if (it.size == 1)
                    it[0]
                else
                    it
            }?.let { UUID.fromString(it as String) }
}
