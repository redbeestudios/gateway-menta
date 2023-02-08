package com.menta.libs.security.ownership.owner.provider.configuration

import com.menta.libs.security.ownership.owner.provider.DefaultOwnerProvider
import com.menta.libs.security.ownership.owner.provider.OwnerProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class OwnerProviderConfiguration {

    @Bean("libsSecurityOwnerProvider")
    @ConditionalOnMissingBean(OwnerProvider::class)
    fun ownerProvider(): OwnerProvider =
        DefaultOwnerProvider()
}
