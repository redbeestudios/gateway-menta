package com.menta.api.users.adapter.`in`.model

import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email as EmailValidation

data class SetUserPasswordRequest(
    @Schema(description = "Type of the user")
    val userType: UserType,

    @Schema(description = "email for the user", type = "string", example = "menta@menta.global.com")
    @field:EmailValidation(
        regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
    )
    val email: Email,

    @Schema(description = "Password for the user")
    val password: String,

    @Schema(description = "Flag that indicates if the password must be permanent")
    val permanent: Boolean,
)
