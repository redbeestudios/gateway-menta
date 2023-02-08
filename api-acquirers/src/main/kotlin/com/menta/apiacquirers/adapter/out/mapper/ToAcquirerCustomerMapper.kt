package com.menta.apiacquirers.adapter.out.mapper

import com.menta.apiacquirers.adapter.out.model.AcquirerCustomerResponse
import com.menta.apiacquirers.domain.AcquirerCustomer
import org.springframework.stereotype.Component

@Component
class ToAcquirerCustomerMapper() {

    fun mapFrom(response: AcquirerCustomerResponse): AcquirerCustomer =
        with(response) {
            AcquirerCustomer(
                customerId = customerId,
                acquirers = listOf(
                    AcquirerCustomer.Acquirer(
                        acquirerId = acquirerId,
                        code = code
                    )
                )
            )
        }
}
