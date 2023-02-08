package com.menta.api.taxesEntities.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide(): UUID = UUID.randomUUID()
}
