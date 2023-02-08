package com.menta.api.feenicia.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConfigurationPropertiesScan("com.menta.api.feenicia")
class AppConfig : WebMvcConfigurer
