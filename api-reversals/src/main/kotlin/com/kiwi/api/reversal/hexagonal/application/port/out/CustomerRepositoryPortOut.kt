package com.kiwi.api.reversal.hexagonal.application.port.out

import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import java.util.UUID

interface CustomerRepositoryPortOut {
    fun retrieve(customerId: UUID): Customer
}
