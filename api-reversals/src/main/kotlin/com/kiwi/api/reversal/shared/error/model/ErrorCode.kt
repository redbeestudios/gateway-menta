package com.kiwi.api.reversal.shared.error.model

enum class ErrorCode(val value: Int, val reason: String) {
    MESSAGE_NOT_READABLE(401, "El mensaje no es legible"),
    ACCESS_DENIED(403, "Acesso denegado"),
    INTERNAL_ERROR(100, "Error interno del servidor");
}
