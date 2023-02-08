package com.menta.libs.security.ownership.annotation.provider

import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import javax.servlet.http.HttpServletRequest

interface EntityOwnershipValidationAnnotationProvider {
    fun provideFrom(request: HttpServletRequest): List<EntityOwnershipValidation>
}
