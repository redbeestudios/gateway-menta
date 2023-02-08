package com.menta.api.credibanco.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConfigurationPropertiesScan("com.menta.api.credibanco")
class AppConfig : WebMvcConfigurer
