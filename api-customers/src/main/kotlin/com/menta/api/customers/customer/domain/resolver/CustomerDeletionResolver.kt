package com.menta.api.customers.customer.domain.resolver

import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CustomerDeletionResolver {

    fun resolveDeletion(customer: Customer) =
        customer.copy(deleteDate = OffsetDateTime.now())
            .log { info("customer deleted with date: {}", it.deleteDate) }

    companion object : CompanionLogger()
}
