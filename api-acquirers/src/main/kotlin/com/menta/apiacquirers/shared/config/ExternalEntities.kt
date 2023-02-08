package com.menta.apiacquirers.shared.config

import com.menta.apiacquirers.adapter.out.AcquirerPath
import com.menta.apiacquirers.adapter.out.OperationPath
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConstructorBinding
@ConfigurationProperties(prefix = "externals.entities")
data class ExternalEntities(
    val operations: List<OperationPath>,
    val acquirers: List<AcquirerPath>
)

@Configuration
class ExternalEntitiesConfig {

    @Bean
    fun operationsPaths(externalEntities: ExternalEntities): List<OperationPath> =
        externalEntities.operations

    @Bean
    fun acquirersPaths(externalEntities: ExternalEntities): List<AcquirerPath> =
        externalEntities.acquirers
}
