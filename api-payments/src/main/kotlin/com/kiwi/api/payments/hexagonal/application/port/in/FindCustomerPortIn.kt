package com.kiwi.api.payments.hexagonal.application.port.`in`

import com.kiwi.api.payments.hexagonal.domain.Payment
import java.util.UUID

interface FindCustomerPortIn {
    fun execute(customerId: UUID): Payment.Customer
}
