package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.Owner
import java.util.UUID
import javax.servlet.http.HttpServletRequest

interface EntityIdentityRequestExtractor {
    fun extract(request: HttpServletRequest, owner: Owner): UUID?
}
