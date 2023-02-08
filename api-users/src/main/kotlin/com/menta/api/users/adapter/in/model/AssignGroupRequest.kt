package com.menta.api.users.adapter.`in`.model

import io.swagger.v3.oas.annotations.media.Schema

data class AssignGroupRequest(
    @Schema(type = "string", example = "Payment::Create", description = "name of the group")
    val name: String
)
