package com.menta.libs.security.requesterUser

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.requesterUser.jwt.provider.FromSecurityContextJwtProvider
import com.menta.libs.security.requesterUser.jwt.provider.JwtProvider
import com.menta.libs.security.requesterUser.model.mapper.ToRequesterUserMapper
import com.menta.libs.security.requesterUser.provider.FromJwtRequesterUserProvider
import com.menta.libs.security.requesterUser.provider.RequesterUserProvider
import com.menta.libs.security.requesterUser.provider.UserTypeProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "libs.security", name = ["requester-user.provider.enabled"])
@Configuration("libsSecurityRequesterUserConfiguration")
class RequesterUserConfiguration {

    @ConditionalOnMissingBean
    @Bean(name = ["libsSecurityFromJwtRequesterUserProvider"])
    fun requesterUserProvider(
        jwtProvider: JwtProvider,
        toRequesterUserMapper: ToRequesterUserMapper
    ): RequesterUserProvider =
        FromJwtRequesterUserProvider(
            jwtProvider = jwtProvider,
            toRequesterUserMapper = toRequesterUserMapper
        )

    @ConditionalOnMissingBean
    @Bean(name = ["libsSecurityFromSecurityContextJwtProvider"])
    fun jwtProvider(): JwtProvider =
        FromSecurityContextJwtProvider()

    @ConditionalOnMissingBean
    @Bean(name = ["libsSecurityToRequestUserMapper"])
    fun toRequestUserMapper(userTypeProvider: UserTypeProvider): ToRequesterUserMapper =
        ToRequesterUserMapper(userTypeProvider = userTypeProvider)

    @ConditionalOnMissingBean
    @Bean(name = ["libsSecurityUserTypeProvider"])
    fun userTypeProvider(properties: SecurityConfigurationProperties): UserTypeProvider =
        UserTypeProvider(properties)
}
