package com.kiwi.api.reversal.hexagonal.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide(): String = UUID.randomUUID().toString()
}
