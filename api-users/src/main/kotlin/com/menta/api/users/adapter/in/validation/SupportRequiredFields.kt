package com.menta.api.users.adapter.`in`.validation

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.domain.UserType.SUPPORT
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SupportRequiredFieldsValidator::class])
annotation class SupportRequiredFields(
    val message: String = "Support users cant have a customer_id or merchant_id",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class SupportRequiredFieldsValidator : ConstraintValidator<SupportRequiredFields, CreateUserRequest> {
    override fun isValid(value: CreateUserRequest?, context: ConstraintValidatorContext?): Boolean =
        value?.let {
            if (it.userType == SUPPORT) {
                it.attributes.merchantId == null && it.attributes.customerId == null
            } else {
                true
            }
        } ?: false
}
