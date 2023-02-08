package com.menta.libs.security.authenticationManager.model

import com.menta.libs.security.SecurityConfigurationProperties.ResourceServer.Issuer
import org.springframework.security.authentication.AuthenticationManager

typealias AuthenticationManagerByIssuer = Map<Issuer, AuthenticationManager>
