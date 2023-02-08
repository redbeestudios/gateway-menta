package com.kiwi.api.reimbursements.hexagonal.application.port.out

import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import java.util.UUID

interface CustomerRepositoryPortOut {
    fun retrieve(customerId: UUID): Customer
}
