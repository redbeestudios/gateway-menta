package com.menta.libs.security.ownership.owner.validator.configuration

import com.menta.libs.security.ownership.owner.validator.OwnerValidator
import com.menta.libs.security.ownership.owner.validator.RequesterUserIsOwnerValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class OwnerValidatorConfiguration {

    @Bean("libsSecurityOwnerValidator")
    @ConditionalOnMissingBean(OwnerValidator::class)
    fun ownerValidator(): OwnerValidator =
        RequesterUserIsOwnerValidator()
}
