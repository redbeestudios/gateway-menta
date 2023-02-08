package com.kiwi.api.payments.application.usecase

import arrow.core.Either
import com.kiwi.api.payments.application.port.`in`.PingInPort
import com.kiwi.api.payments.application.port.out.GlobalClientRepository
import com.kiwi.api.payments.shared.error.model.ApplicationError
import org.springframework.stereotype.Component

@Component
class PingUseCase(private val globalClientRepository: GlobalClientRepository) : PingInPort {

    override fun execute(): Either<ApplicationError, Unit> =
        globalClientRepository.ping()
}
