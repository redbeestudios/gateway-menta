package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.Owner
import org.springframework.web.servlet.HandlerMapping
import java.util.UUID
import org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
import javax.servlet.http.HttpServletRequest

interface FromPathEntityIdentityRequestExtractor : EntityIdentityRequestExtractor

class DefaultFromPathEntityIdentityRequestExtractor : FromPathEntityIdentityRequestExtractor {
    override fun extract(request: HttpServletRequest, owner: Owner): UUID? =
        request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)
            ?.takeIf { it is Map<*, *> }
            ?.let { (it as Map<*, *>)[owner.argumentName] }
            ?.let { UUID.fromString(it as String) }
}
