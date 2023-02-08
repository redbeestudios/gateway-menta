package com.kiwi.api.payments.hexagonal.adapter.out.db.resolver

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CreateDatetimeResolver {
    fun provide() = OffsetDateTime.now()
}
