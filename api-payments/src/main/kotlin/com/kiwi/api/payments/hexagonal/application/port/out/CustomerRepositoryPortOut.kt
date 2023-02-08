package com.kiwi.api.payments.hexagonal.application.port.out

import com.kiwi.api.payments.hexagonal.domain.Payment
import java.util.UUID

interface CustomerRepositoryPortOut {
    fun retrieve(customerId: UUID): Payment.Customer
}
