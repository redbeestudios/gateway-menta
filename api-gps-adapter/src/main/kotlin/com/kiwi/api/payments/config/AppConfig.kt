package com.kiwi.api.payments.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConfigurationPropertiesScan("com.kiwi.api.payments")
class AppConfig : WebMvcConfigurer
