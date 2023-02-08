package com.menta.api.customers.acquirer.adapter.`in`.model.mapper

import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerRequest
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToPreAcquirerCustomerMapper {

    fun map(acquirerCustomerRequest: AcquirerCustomerRequest, customerId: UUID): PreAcquirerCustomer =
        with(acquirerCustomerRequest) {
            PreAcquirerCustomer(
                customerId = customerId,
                acquirerId = acquirerId,
                code = code
            )
        }
}
