package com.kiwi.api.payments.application.port.out

import com.kiwi.api.payments.domain.AcquirerCustomer
import java.util.UUID

interface AcquirerCustomerRepositoryOutPort {
    fun findBy(customerId: UUID): AcquirerCustomer?
}
