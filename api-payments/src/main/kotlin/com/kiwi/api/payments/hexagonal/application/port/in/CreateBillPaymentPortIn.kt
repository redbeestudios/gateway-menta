package com.kiwi.api.payments.hexagonal.application.port.`in`

import arrow.core.Either
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface CreateBillPaymentPortIn {
    fun execute(payment: Payment): Either<ApplicationError, CreatedPayment>
}
