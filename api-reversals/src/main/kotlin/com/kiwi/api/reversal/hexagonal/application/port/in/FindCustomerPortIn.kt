package com.kiwi.api.reversal.hexagonal.application.port.`in`

import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import java.util.UUID

interface FindCustomerPortIn {
    fun execute(customerId: UUID): Customer
}
