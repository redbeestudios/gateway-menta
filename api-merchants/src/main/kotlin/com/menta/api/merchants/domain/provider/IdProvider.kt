package com.menta.api.merchants.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide() = UUID.randomUUID()
}
