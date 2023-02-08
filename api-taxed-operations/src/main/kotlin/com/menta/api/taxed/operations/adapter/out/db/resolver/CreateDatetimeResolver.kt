package com.menta.api.taxed.operations.adapter.out.db.resolver

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CreateDatetimeResolver {
    fun provide() = OffsetDateTime.now()
}
