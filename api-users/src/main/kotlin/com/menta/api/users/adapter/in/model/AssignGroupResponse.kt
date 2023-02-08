package com.menta.api.users.adapter.`in`.model

import io.swagger.v3.oas.annotations.media.Schema

data class AssignGroupResponse(
    val user: UserResponse,
    val group: Group
) {
    data class Group(
        @Schema(type = "string", example = "Payment::Create", description = "name of the group used")
        val name: String
    )
}
