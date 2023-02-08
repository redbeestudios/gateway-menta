package com.menta.libs.security

import com.menta.libs.security.ownership.filter.EntityOwnershipValidationFilter
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.http.HttpServletRequest

@EnableWebSecurity // Enable Spring Security’s web
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // To configure method-level security
class SecurityConfig(
    private val properties: SecurityConfigurationProperties
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(
        http: HttpSecurity,
        authenticationManagerResolver: AuthenticationManagerResolver<HttpServletRequest>,
        entityOwnershipValidator: EntityOwnershipValidationFilter?,
        exceptionHandling: ExceptionHandlingFilter
        ): SecurityFilterChain {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
            .csrf()
            .ignoringAntMatchers(*properties.ignorePaths.toTypedArray())
            .and()
            .authorizeRequests()
            .antMatchers(*properties.ignorePaths.toTypedArray()).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(exceptionHandling, BearerTokenAuthenticationFilter::class.java)
            .let {
                if (properties.ownership.enabled) {
                    it.addFilterAfter(entityOwnershipValidator!!, BearerTokenAuthenticationFilter::class.java)
                } else {
                    it
                }
            }
            .oauth2ResourceServer()
            .authenticationManagerResolver(authenticationManagerResolver)

        return http.build()
    }

}
