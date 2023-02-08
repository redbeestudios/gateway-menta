package com.kiwi.api.batchcloses.hexagonal.application.usecase

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

fun interface ProvideIdUseCase {
    fun provide(): String
}

@Configuration
class ProvideBatchCloseIdUseCaseImpls {

    @Bean("provideBatchCloseIdUseCase")
    fun provideIdUseCase() =
        ProvideIdUseCase { UUID.randomUUID().toString() }
}
