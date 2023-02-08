package com.menta.api.merchants.acquirer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.UUID

@Document(collection = "acquirerMerchants")
@CompoundIndex(name = "acquirer_merchantId", def = "{'acquirer': 1, 'merchantId' : 1}", unique = true)
data class AcquirerMerchant(
    @Id
    val id: UUID,
    val merchantId: UUID,
    val acquirer: String,
    val code: String,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime
)
