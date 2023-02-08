package com.menta.apisecrets.adapter.out.model

import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Country
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("config")
data class OperableAcquirer(
    val acquirers: List<AcquirerConfig>
) {
    data class AcquirerConfig(
        val name: Acquirer,
        val country: List<Country>
    )
}
