package com.menta.api.customers.acquirer.adapter.`in`.model.mapper

import com.menta.api.customers.acquirer.adapter.`in`.model.AcquirerCustomerResponse
import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerCustomerResponseMapper {

    fun mapFrom(acquirerCustomer: AcquirerCustomer) =
        with(acquirerCustomer) {
            AcquirerCustomerResponse(
                customerId = customerId.toString(),
                acquirerId = acquirerId.toString(),
                code = code,
                createDate = createDate,
                updateDate = updateDate
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
