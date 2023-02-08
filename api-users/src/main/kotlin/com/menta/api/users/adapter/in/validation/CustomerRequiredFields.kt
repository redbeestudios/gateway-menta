package com.menta.api.users.adapter.`in`.validation

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.domain.UserType
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CustomerRequiredFieldsValidator::class])
annotation class CustomerRequiredFields(
    val message: String = "Customer users must have a valid customer_id and not have a merchant_id",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CustomerRequiredFieldsValidator : ConstraintValidator<CustomerRequiredFields, CreateUserRequest> {
    override fun isValid(value: CreateUserRequest?, context: ConstraintValidatorContext?): Boolean =
        value?.let {
            if (it.userType == UserType.CUSTOMER) {
                it.attributes.customerId != null && it.attributes.merchantId == null
            } else {
                true
            }
        } ?: false
}
