package com.kiwi.api.reverse.config

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.filter.CommonsRequestLoggingFilter
import java.text.SimpleDateFormat


@Configuration
class HttpConfiguration {

    @Primary
    @Bean("reimbursementsMapperBuilder")
    fun jackson2ObjectMapperBuilder() = Jackson2ObjectMapperBuilder()
        .failOnUnknownProperties(false)
        .serializationInclusion(NON_NULL)
        .featuresToEnable(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .propertyNamingStrategy(SNAKE_CASE)
        .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))

    @Bean
    fun requestLoggingFilter() = CommonsRequestLoggingFilter()
        .apply {
            setIncludeClientInfo(true)
            setIncludeQueryString(true)
            setIncludePayload(true)
            setIncludeHeaders(false)
        }
}
