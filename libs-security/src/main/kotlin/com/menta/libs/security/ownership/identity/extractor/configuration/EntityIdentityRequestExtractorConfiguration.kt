package com.menta.libs.security.ownership.identity.extractor.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.menta.libs.security.ownership.filter.EntityOwnershipValidationFilter
import com.menta.libs.security.ownership.identity.extractor.DefaultFromBodyEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.DefaultFromPathEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.DefaultFromQueryParamEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.EntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.EntityIdentityRequestExtractorStrategy
import com.menta.libs.security.ownership.identity.extractor.FromBodyEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.FromPathEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.identity.extractor.FromQueryParamEntityIdentityRequestExtractor
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.BODY_ATTRIBUTE
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.QUERY_PARAMETER
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class EntityIdentityRequestExtractorConfiguration {

    @Bean("libsSecurityEntityIdentityRequestExtractorStrategy")
    @ConditionalOnMissingBean(EntityIdentityRequestExtractorStrategy::class)
    fun extractorStrategy(
        fromBodyEntityIdentityRequestExtractor: FromBodyEntityIdentityRequestExtractor,
        fromPathEntityIdentityRequestExtractor: FromPathEntityIdentityRequestExtractor,
        fromQueryParamEntityIdentityRequestExtractor: FromQueryParamEntityIdentityRequestExtractor
    ): EntityIdentityRequestExtractorStrategy =
        EntityIdentityRequestExtractorStrategy(
            mapOf(
                BODY_ATTRIBUTE to fromBodyEntityIdentityRequestExtractor,
                PATH_VARIABLE to fromPathEntityIdentityRequestExtractor,
                QUERY_PARAMETER to fromQueryParamEntityIdentityRequestExtractor
            )
        )

    @Bean("libsSecurityFromBodyEntityIdentityRequestExtractor")
    @ConditionalOnMissingBean(FromBodyEntityIdentityRequestExtractor::class)
    fun fromBodyEntityIdentityRequestExtractor(objectMapper: ObjectMapper): FromBodyEntityIdentityRequestExtractor =
        DefaultFromBodyEntityIdentityRequestExtractor(objectMapper)

    @Bean("libsSecurityFromPathEntityIdentityRequestExtractor")
    @ConditionalOnMissingBean(FromPathEntityIdentityRequestExtractor::class)
    fun fromPathEntityIdentityRequestExtractor(): FromPathEntityIdentityRequestExtractor =
        DefaultFromPathEntityIdentityRequestExtractor()

    @Bean("libsSecurityFromQueryParamEntityIdentityRequestExtractor")
    @ConditionalOnMissingBean(FromQueryParamEntityIdentityRequestExtractor::class)
    fun fromQueryParamEntityIdentityRequestExtractor(): FromQueryParamEntityIdentityRequestExtractor =
        DefaultFromQueryParamEntityIdentityRequestExtractor()
}
