package com.menta.apisecrets.adapter.out.model.mapper

import com.menta.apisecrets.aCustomerResponse
import com.menta.apisecrets.domain.Country.ARG
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.invalidCountry
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class ToCustomerMapperSpec : FeatureSpec({

    val mapper = ToCustomerMapper()

    feature("map customer from response") {

        scenario("customer with valid country") {
            aCustomerResponse().let {
                mapper.from(it) shouldBeRight Customer(id = it.id, country = ARG)
            }
        }

        scenario("customer with valid country with different casing") {
            aCustomerResponse().copy(country = "ARG").let {
                mapper.from(it) shouldBeRight Customer(id = it.id, country = ARG)
            }
        }

        scenario("customer with invalid country") {
            aCustomerResponse().copy(country = "ABC").let {
                mapper.from(it) shouldBeLeft invalidCountry("ABC")
            }
        }
    }
})
