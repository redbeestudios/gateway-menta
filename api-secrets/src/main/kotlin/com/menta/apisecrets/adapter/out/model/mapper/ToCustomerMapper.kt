package com.menta.apisecrets.adapter.out.model.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import com.menta.apisecrets.adapter.out.model.CustomerResponse
import com.menta.apisecrets.domain.Country
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.util.log.CompanionLogger
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class ToCustomerMapper {

    fun from(response: CustomerResponse): Either<ApplicationError, Customer> =
        runBlocking {
            either<ApplicationError, Customer> {
                Customer(
                    id = response.id,
                    country = Country.getBy(response.country).bind()
                ).right().bind()
            }
        }.logEither(
            { error("error mapping customer: {}", it) },
            { info("customer mapped: {}", it) }
        )

    companion object : CompanionLogger()
}
