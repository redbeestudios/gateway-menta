package com.menta.api.users.domain

enum class UserStatus {
    UNCONFIRMED, CONFIRMED, ARCHIVED, UNKNOWN, RESET_REQUIRED, FORCE_CHANGE_PASSWORD, COMPROMISED;
}
