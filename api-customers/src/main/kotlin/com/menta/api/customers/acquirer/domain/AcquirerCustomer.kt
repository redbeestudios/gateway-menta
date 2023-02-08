package com.menta.api.customers.acquirer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.UUID

@Document(collection = "acquirerCustomers")
@CompoundIndex(name = "customerId_acquirerId", def = "{'customerId' : 1, 'acquirerId': 1}", unique = true)
data class AcquirerCustomer(
    @Id
    val id: UUID,
    val customerId: UUID,
    val acquirerId: String,
    val code: String,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime
)
