package com.menta.api.transactions.config

import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import com.menta.libs.security.ownership.annotation.EntityOwnershipValidations
import com.menta.libs.security.ownership.annotation.provider.EntityOwnershipValidationAnnotationProvider
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.ServletRequestPathUtils
import javax.servlet.http.HttpServletRequest

@Component
class EntityOwnershipValidationAnnotationProvider(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : EntityOwnershipValidationAnnotationProvider {

    override fun provideFrom(request: HttpServletRequest): List<EntityOwnershipValidation> =
        getHandlerMethod(request)?.let {
            it.getMethodAnnotation(EntityOwnershipValidations::class.java)
                ?.validations
                ?.toList()
                ?: it.getMethodAnnotation(EntityOwnershipValidation::class.java)
                    ?.let { listOf(it) }
        } ?: emptyList()

    private fun getHandlerMethod(request: HttpServletRequest): HandlerMethod? {
        if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
            ServletRequestPathUtils.parseAndCache(request)
        }
        return requestMappingHandlerMapping
            .getHandler(request)
            ?.handler
            ?.let { it as HandlerMethod }
    }
}
