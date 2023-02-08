package com.menta.apiacquirers.adapter.`in`.model.mapper

import com.menta.apiacquirers.adapter.`in`.model.OperableResponse
import com.menta.apiacquirers.domain.AcquirerCustomer
import org.springframework.stereotype.Component

@Component
class ToOperableResponseMapper() {

    fun mapFrom(response: AcquirerCustomer): OperableResponse =
        with(response) {
            OperableResponse(
                customerId = customerId,
                acquirers = acquirers.map { acquirer ->
                    OperableResponse.Acquirer(
                        acquirerId = acquirer.acquirerId,
                        code = acquirer.code
                    )
                }
            )
        }
}
