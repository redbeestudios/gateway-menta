package com.kiwi.api.reverse.hexagonal.application.usecase

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

fun interface ProvideIdUseCase {
    fun provide(): String
}

@Configuration
class ProvideReimbursementIdUseCaseImpls {

    @Bean("provideReimbursementIdUseCase")
    fun provideIdUseCase() =
        ProvideIdUseCase { UUID.randomUUID().toString() }
}

@Configuration
class ProvidePaymentIdUseCaseImpls {

    @Bean("providePaymentIdUseCase")
    fun provideIdUseCase() =
        ProvideIdUseCase { UUID.randomUUID().toString() }
}

@Configuration
class ProvideBatchCloseIdUseCaseImpls {

    @Bean("provideBatchCloseIdUseCase")
    fun provideIdUseCase() =
        ProvideIdUseCase { UUID.randomUUID().toString() }
}
