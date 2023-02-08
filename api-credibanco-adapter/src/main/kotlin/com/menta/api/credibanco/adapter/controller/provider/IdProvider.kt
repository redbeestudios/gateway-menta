package com.menta.api.credibanco.adapter.controller.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdProvider {
    fun provide(): UUID = UUID.randomUUID()
}
