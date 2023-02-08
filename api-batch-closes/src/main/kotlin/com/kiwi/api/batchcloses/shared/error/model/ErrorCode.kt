package com.kiwi.api.batchcloses.shared.error.model

enum class ErrorCode(val value:Int, val reason:String) {
        MESSAGE_NOT_READABLE(401, "El mensaje no es legible"),
        INTERNAL_ERROR(100, "Error interno del servidor");
}
