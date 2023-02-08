package com.menta.api.merchants.domain.provider

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CodeProvider {
    fun provide(id: UUID, code: String?): String =
        if (code?.isNotEmpty() == true) {
            code
        } else {
            id.toString().replace("-", "").substring(0, 15)
        }
}
