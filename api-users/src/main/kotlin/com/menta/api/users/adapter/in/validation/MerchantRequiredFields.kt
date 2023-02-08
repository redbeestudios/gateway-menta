package com.menta.api.users.adapter.`in`.validation

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.domain.UserType.MERCHANT
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [MerchantRequiredFieldsValidator::class])
annotation class MerchantRequiredFields(
    val message: String = "Merchant users must have a valid customer_id and merchant_id",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class MerchantRequiredFieldsValidator : ConstraintValidator<MerchantRequiredFields, CreateUserRequest> {
    override fun isValid(value: CreateUserRequest?, context: ConstraintValidatorContext?): Boolean =
        value?.let {
            if (it.userType == MERCHANT) {
                it.attributes.merchantId != null && it.attributes.customerId != null
            } else {
                true
            }
        } ?: false
}
