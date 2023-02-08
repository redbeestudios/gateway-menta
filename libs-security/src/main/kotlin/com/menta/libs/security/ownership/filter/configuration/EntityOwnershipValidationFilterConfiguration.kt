package com.menta.libs.security.ownership.filter.configuration

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.ownership.annotation.provider.EntityOwnershipValidationAnnotationProvider
import com.menta.libs.security.ownership.filter.EntityOwnershipValidationFilter
import com.menta.libs.security.ownership.identity.validator.EntityIdentityValidatorStrategy
import com.menta.libs.security.ownership.owner.provider.OwnerProvider
import com.menta.libs.security.ownership.owner.validator.OwnerValidator
import com.menta.libs.security.requesterUser.provider.RequesterUserProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class EntityOwnershipValidationFilterConfiguration {

    @Bean("libsSecurityEntityOwnershipValidationFilter")
    @ConditionalOnMissingBean(EntityOwnershipValidationFilter::class)
    fun filter(
        requesterUserProvider: RequesterUserProvider,
        ownerProvider: OwnerProvider,
        entityOwnershipValidationAnnotationProvider: EntityOwnershipValidationAnnotationProvider,
        entityIdentityValidator: EntityIdentityValidatorStrategy,
        requesterUserIsOwnerValidator: OwnerValidator,
        properties: SecurityConfigurationProperties
    ): EntityOwnershipValidationFilter =
        EntityOwnershipValidationFilter(
            requesterUserProvider = requesterUserProvider,
            ownerProvider = ownerProvider,
            entityOwnershipValidationAnnotationProvider = entityOwnershipValidationAnnotationProvider,
            entityIdentityValidator = entityIdentityValidator,
            requesterUserIsOwnerValidator = requesterUserIsOwnerValidator,
            properties = properties
        )
}
