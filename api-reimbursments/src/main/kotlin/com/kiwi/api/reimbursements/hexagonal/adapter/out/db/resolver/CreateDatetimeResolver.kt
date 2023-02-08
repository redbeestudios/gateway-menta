package com.kiwi.api.reimbursements.hexagonal.adapter.out.db.resolver

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class CreateDatetimeResolver {
    fun provide(): OffsetDateTime = OffsetDateTime.now()
}
