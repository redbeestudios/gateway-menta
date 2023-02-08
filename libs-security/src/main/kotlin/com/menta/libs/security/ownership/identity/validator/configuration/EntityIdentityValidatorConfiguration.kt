package com.menta.libs.security.ownership.identity.validator.configuration

import com.menta.libs.security.ownership.identity.provider.OwnerIdentityProvider
import com.menta.libs.security.ownership.identity.validator.CustomerIdentityValidator
import com.menta.libs.security.ownership.identity.validator.DefaultCustomerIdentityValidator
import com.menta.libs.security.ownership.identity.validator.DefaultMerchantIdentityValidator
import com.menta.libs.security.ownership.identity.validator.DefaultSupportIdentityValidator
import com.menta.libs.security.ownership.identity.validator.EntityIdentityValidatorStrategy
import com.menta.libs.security.ownership.identity.validator.MerchantIdentityValidator
import com.menta.libs.security.ownership.identity.validator.SupportIdentityValidator
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.SUPPORT
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "libs.security", name = ["ownership.enabled"])
class EntityIdentityValidatorConfiguration {

    @Bean("libsSecuritySupportIdentityValidator")
    @ConditionalOnMissingBean(SupportIdentityValidator::class)
    fun supportIdentityValidator(): SupportIdentityValidator =
        DefaultSupportIdentityValidator()

    @Bean("libsSecurityMerchantIdentityValidator")
    @ConditionalOnMissingBean(MerchantIdentityValidator::class)
    fun merchantIdentityValidator(ownerIdentityProvider: OwnerIdentityProvider): MerchantIdentityValidator =
        DefaultMerchantIdentityValidator(ownerIdentityProvider)

    @Bean("libsSecurityCustomerIdentityValidator")
    @ConditionalOnMissingBean(CustomerIdentityValidator::class)
    fun customerIdentityValidator(ownerIdentityProvider: OwnerIdentityProvider): CustomerIdentityValidator =
        DefaultCustomerIdentityValidator(ownerIdentityProvider)

    @Bean("libsSecurityEntityIdentityValidatorStrategy")
    @ConditionalOnMissingBean(EntityIdentityValidatorStrategy::class)
    fun entityIdentityValidatorStrategy(
        customerIdentityValidator: CustomerIdentityValidator,
        merchantIdentityValidator: MerchantIdentityValidator,
        supportIdentityValidator: SupportIdentityValidator
    ): EntityIdentityValidatorStrategy =
        EntityIdentityValidatorStrategy(
            mapOf(
                SUPPORT to supportIdentityValidator,
                MERCHANT to merchantIdentityValidator,
                CUSTOMER to customerIdentityValidator
            )
        )
}
