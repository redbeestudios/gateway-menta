package com.menta.api.users.adapter.`in`.model

import com.menta.api.users.adapter.`in`.validation.CustomerRequiredFields
import com.menta.api.users.adapter.`in`.validation.MerchantRequiredFields
import com.menta.api.users.adapter.`in`.validation.SupportRequiredFields
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Email as EmailValidation

@MerchantRequiredFields
@CustomerRequiredFields
@SupportRequiredFields
data class CreateUserRequest(
    @Schema(description = "Type of the user")
    val userType: UserType,

    @Schema(description = "Parameters needed create a user")
    @field:Valid
    val attributes: Attributes,

) {

    data class Attributes(
        @Schema(description = "email for the user", type = "string", example = "menta@menta.global.com")
        @field:EmailValidation(
            regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
        )
        val email: Email,

        @Schema(
            description = "id of merchant the user belongs to, must be present if user_type is MERCHANT",
            required = false,
            example = "e9d5141b-6cf3-4c13-8226-be5e14474ae6"
        )
        val merchantId: UUID?,

        @Schema(
            description = "id of customer the user/merchant belongs to, must be present if user_type is CUSTOMER or MERCHANT",
            required = false,
            example = "c44eb3c2-101a-4b4c-8350-b1b7fde949e5"
        )
        val customerId: UUID?
    )
}
