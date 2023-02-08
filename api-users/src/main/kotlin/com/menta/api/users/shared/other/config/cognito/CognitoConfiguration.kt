package com.menta.api.users.shared.other.config.cognito

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CognitoConfiguration(
    private val properties: CognitoConfigurationProperties
) {

    @Bean()
    @ConditionalOnProperty(prefix = "cognito.provider.credentials", name = ["secret", "accessKey"])
    fun awsCredentials(): AWSCredentials =
        with(properties.provider.credentials!!) {
            BasicAWSCredentials(accessKey, secret)
        }

    @Bean
    @ConditionalOnBean(AWSCredentials::class)
    fun awsCredentialsProvider(credentials: AWSCredentials): AWSCredentialsProvider =
        AWSStaticCredentialsProvider(credentials)

    @Bean
    fun cognitoIdentityProvider(credentialsProvider: AWSCredentialsProvider?): AWSCognitoIdentityProvider =
        with(properties.provider) {
            AWSCognitoIdentityProviderClientBuilder
                .standard()
                .apply { credentialsProvider?.let { withCredentials(it) } }
                .withRegion(region)
                .build()
        }
}
