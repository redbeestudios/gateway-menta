package com.menta.api.customers.customer.domain.resolver

import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CustomerUpdateResolver {

    fun resolveUpdate(customer: Customer) =
        customer.copy(updateDate = OffsetDateTime.now())
            .log { info("customer updated with date: {}", it.updateDate) }

    companion object : CompanionLogger()
}
