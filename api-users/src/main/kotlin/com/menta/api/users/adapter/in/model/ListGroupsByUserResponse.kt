package com.menta.api.users.adapter.`in`.model

import java.util.Date

data class ListGroupsByUserResponse(
    val user: UserResponse,
    val groups: List<Group>,
    val _metadata: SearchMetadata
) {
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

    data class SearchMetadata(
        val _next: String?,
        val _limit: Int?
    )
}
