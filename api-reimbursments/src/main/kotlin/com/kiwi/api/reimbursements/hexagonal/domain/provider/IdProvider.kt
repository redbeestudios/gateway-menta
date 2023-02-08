package com.kiwi.api.reimbursements.hexagonal.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide(): UUID = UUID.randomUUID()
}
