package com.kiwi.api.payments.domain.field.provider

import com.kiwi.api.payments.domain.AcquirerCustomer
import com.kiwi.api.payments.domain.AcquirerMerchant
import com.kiwi.api.payments.domain.Address
import com.kiwi.api.payments.domain.Customer
import com.kiwi.api.payments.domain.Merchant
import com.kiwi.api.payments.domain.State
import com.kiwi.api.payments.domain.field.Aggregator
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class AggregatorProvider {

    fun provide(customer: Customer, merchant: Merchant, acquirerCustomer: AcquirerCustomer, acquirerMerchant: AcquirerMerchant): Aggregator =
        Aggregator(
            name = customer.fantasyName,
            commerceCode = acquirerCustomer.code,
            address = buildAddress(customer.address),
            childCommerce = Aggregator.ChildCommerce(
                name = merchant.fantasyName,
                code = acquirerMerchant.code,
                state = State.valueOf(merchant.address.state),
                city = merchant.address.city,
                zip = merchant.address.zip,
                categoryCode = merchant.category
            )
        ).log { info("aggregator provided: {}", it) }

    private fun buildAddress(address: Address): String =
        "${address.street} ${address.number} ${address.city}, ${address.state}"

    companion object : CompanionLogger()
}
