package com.kiwi.api.payments.application.port.`in`

import com.kiwi.api.payments.domain.AcquirerCustomer
import java.util.UUID

interface FindAcquirerCustomerInPort {
    fun execute(customerId: UUID): AcquirerCustomer?
}
