package com.menta.libs.security.ownership.identity.provider.configuration

import com.menta.libs.security.ownership.identity.extractor.EntityIdentityRequestExtractorStrategy
import com.menta.libs.security.ownership.identity.provider.DefaultOwnerIdentityProvider
import com.menta.libs.security.ownership.identity.provider.OwnerIdentityProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class OwnerIdentityProviderConfiguration {

    @Bean("libsSecurityOwnerIdentityProvider")
    @ConditionalOnMissingBean(OwnerIdentityProvider::class)
    fun ownerIdentityProvider(extractorStrategy: EntityIdentityRequestExtractorStrategy): OwnerIdentityProvider =
        DefaultOwnerIdentityProvider(extractorStrategy)
}
