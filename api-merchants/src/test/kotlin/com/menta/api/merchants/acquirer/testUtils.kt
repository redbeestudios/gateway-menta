package com.menta.api.merchants.acquirer

import com.menta.api.merchants.acquirer.adapter.`in`.model.AcquirerMerchantResponse
import com.menta.api.merchants.acquirer.domain.Acquirer
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

val aMerchantId = UUID.fromString("09c7395f-41e5-4d5c-8b40-f332894a9c4e")
val aCustomerId = UUID.fromString("09c7395f-41e5-4d5c-8b40-f332894a9c4e")
val id = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
const val anAcquirerId = "GPS"
val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

val anAcquirerMerchant = AcquirerMerchant(
    id = id,
    merchantId = aMerchantId,
    acquirer = anAcquirerId,
    code = "23456789",
    createDate = datetime,
    updateDate = datetime
)

val aPreAcquirerMerchant = PreAcquirerMerchant(
    merchantId = aMerchantId,
    acquirerId = anAcquirerId,
    code = "23456789"
)

val anAcquirerMerchantResponse = AcquirerMerchantResponse(
    merchantId = aMerchantId,
    acquirer = anAcquirerId,
    code = "23456789",
    createDate = datetime,
    updateDate = datetime
)

val anAcquirer = Acquirer(
    id = anAcquirerId
)
