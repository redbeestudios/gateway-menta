package com.menta.api.banorte.config

import co.elastic.apm.attach.ElasticApmAttacher
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConfigurationProperties(prefix = "elastic.apm")
@ConditionalOnProperty(value = ["elastic.apm.enabled"], havingValue = "true")
class ElasticApmConfig {
    private val SERVER_URL_KEY = "server_url"
    @Value("\${elastic.apm.server-url}")
    private val serverUrl: String? = null

    private val SERVICE_NAME_KEY = "service_name"
    @Value("\${elastic.apm.service-name}")
    private val serviceName: String? = null

    private val SECRET_TOKEN_KEY = "secret_token"
    @Value("\${elastic.apm.secret-token}")
    private val secretToken: String? = null

    private val ENVIRONMENT_KEY = "environment"
    @Value("\${elastic.apm.environment}")
    private val environment: String? = null

    private val APPLICATION_PACKAGES_KEY = "application_packages"
    @Value("\${elastic.apm.application-packages}")
    private val applicationPackages: String? = null

    private val LOG_LEVEL_KEY = "log_level"
    @Value("\${elastic.apm.log-level}")
    private val logLevel: String? = null

    @PostConstruct
    fun init() {
        val apmProps: MutableMap<String, String?> = HashMap(6)
        apmProps[SERVER_URL_KEY] = serverUrl
        apmProps[SERVICE_NAME_KEY] = serviceName
        apmProps[SECRET_TOKEN_KEY] = secretToken
        apmProps[ENVIRONMENT_KEY] = environment
        apmProps[APPLICATION_PACKAGES_KEY] = applicationPackages
        apmProps[LOG_LEVEL_KEY] = logLevel
        ElasticApmAttacher.attach(apmProps)
    }
}
