package com.menta.api.customers.acquirer.application.mapper

import com.menta.api.customers.acquirer.domain.AcquirerCustomer
import com.menta.api.customers.acquirer.domain.PreAcquirerCustomer
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.customer.domain.provider.IdProvider
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerCustomerMapper(
    private val idProvider: IdProvider,
    private val dateProvider: DateProvider
) {
    fun map(preAcquirerCustomer: PreAcquirerCustomer) =
        with(preAcquirerCustomer) {
            AcquirerCustomer(
                id = idProvider.provide(),
                customerId = customerId,
                acquirerId = acquirerId,
                code = code,
                createDate = dateProvider.provide(),
                updateDate = dateProvider.provide()
            )
        }.log { info("created acquirer customer mapped: {}", it) }

    companion object : CompanionLogger()
}
