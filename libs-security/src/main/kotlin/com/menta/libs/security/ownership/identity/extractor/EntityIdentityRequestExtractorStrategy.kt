package com.menta.libs.security.ownership.identity.extractor

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource
import com.menta.libs.security.ownership.owner.Owner
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class EntityIdentityRequestExtractorStrategy(
    private val extractors: Map<EntityOwnershipArgumentSource, EntityIdentityRequestExtractor>
) : EntityIdentityRequestExtractor {
    override fun extract(request: HttpServletRequest, owner: Owner): UUID =
        extractors[owner.argumentSource]?.extract(request, owner)
            ?: throw OwnerIdentityNotFoundException(owner)
}

class OwnerIdentityNotFoundException(owner: Owner) :
    RuntimeException("missing ${owner.type} owner in ${owner.argumentSource} request source")
