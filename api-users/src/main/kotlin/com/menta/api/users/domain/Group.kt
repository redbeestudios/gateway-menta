package com.menta.api.users.domain

import java.util.Date

data class Group(
    val name: String,
    val description: String?,
    val audit: Audit
) {
    data class Audit(
        val creationDate: Date,
        val updateDate: Date
    )
}
