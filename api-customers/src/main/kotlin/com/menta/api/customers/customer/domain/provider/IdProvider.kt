package com.menta.api.customers.customer.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide() = UUID.randomUUID()
}
