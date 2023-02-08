package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import com.kiwi.api.reimbursements.hexagonal.domain.Customer
import java.util.UUID

interface FindCustomerPortIn {
    fun execute(customerId: UUID): Customer
}
