package com.kiwi.api.payments.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class AcquirerCustomer(
    @JsonProperty("customer_id")
    val customerId: String,
    @JsonProperty("acquirer_id")
    val acquirerId: String,
    val code: String
)
