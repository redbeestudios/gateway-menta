package com.menta.api.users.adapter.`in`.model

data class ListUsersResponse(
    val users: List<UserResponse>,
    val _metadata: SearchMetadata
) {

    data class SearchMetadata(
        val _next: String?,
        val _limit: Int?
    )
}
