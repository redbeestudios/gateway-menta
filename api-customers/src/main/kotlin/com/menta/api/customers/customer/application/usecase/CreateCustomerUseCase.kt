package com.menta.api.customers.customer.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.customers.customer.application.mapper.ToCustomerMapper
import com.menta.api.customers.customer.application.port.`in`.CreateCustomerPortIn
import com.menta.api.customers.customer.application.port.out.CustomerRepositoryOutPort
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.LegalTypeValidator
import com.menta.api.customers.customer.domain.PreCustomer
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.customerExists
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreateCustomerUseCase(
    private val toCustomerMapper: ToCustomerMapper,
    private val validator: LegalTypeValidator,
    private val customerRepository: CustomerRepositoryOutPort
) : CreateCustomerPortIn {

    override fun execute(
        preCustomer: PreCustomer,
        existingCustomer: Optional<Customer>
    ): Either<ApplicationError, Customer> =
        existingCustomer.shouldNotExist().flatMap {
            preCustomer
                .validateLegalType().map {
                    it.toCustomer()
                        .save()
                        .log { info("Customer {} created with id {}", it.businessName, it.id) }
                }
        }

    private fun Optional<Customer>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            customerExists().left()
        }

    private fun Customer.save() = customerRepository.create(this)

    private fun PreCustomer.toCustomer(): Customer = toCustomerMapper.map(this)

    private fun PreCustomer.validateLegalType() =
        validator.validate(this)
            .logRight { info("Legal Type Validated") }

    companion object : CompanionLogger()
}
