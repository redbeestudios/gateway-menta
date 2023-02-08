package com.menta.api.customers.customer.domain.provider

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CreateDateProvider {
    fun provide() = OffsetDateTime.now()
}
