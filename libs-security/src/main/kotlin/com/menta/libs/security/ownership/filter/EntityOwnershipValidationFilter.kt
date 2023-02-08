package com.menta.libs.security.ownership.filter

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.ownership.annotation.provider.EntityOwnershipValidationAnnotationProvider
import com.menta.libs.security.ownership.identity.validator.EntityIdentityValidatorStrategy
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.ownership.owner.provider.OwnerProvider
import com.menta.libs.security.ownership.owner.validator.OwnerValidator
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.provider.RequesterUserProvider
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class EntityOwnershipValidationFilter(
    private val requesterUserProvider: RequesterUserProvider,
    private val ownerProvider: OwnerProvider,
    private val entityOwnershipValidationAnnotationProvider: EntityOwnershipValidationAnnotationProvider,
    private val entityIdentityValidator: EntityIdentityValidatorStrategy,
    private val requesterUserIsOwnerValidator: OwnerValidator,
    private val properties: SecurityConfigurationProperties
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) =
        validate(request, request.getOwners(), getRequesterUser())
            .let { filterChain.doFilter(request, response) }


    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        properties.ignorePaths.map {
            AntPathMatcher().match(it, request.requestURI)
        }.contains(true)

    private fun getRequesterUser() =
        requesterUserProvider.provide()

    private fun HttpServletRequest.getOwners() =
        ownerProvider.provideFrom(entityOwnershipValidationAnnotationProvider.provideFrom(this))

    private fun RequesterUser.getMatchingOwner(owners: List<Owner>): Owner =
        requesterUserIsOwnerValidator.validate(owners, this)

    private fun validate(request: HttpServletRequest, owners: List<Owner>, requesterUser: RequesterUser) {
        if (owners.isNotEmpty()) {
            requesterUser.getMatchingOwner(owners).let {
                entityIdentityValidator.validate(request, it, requesterUser)
            }
        }
    }
}
