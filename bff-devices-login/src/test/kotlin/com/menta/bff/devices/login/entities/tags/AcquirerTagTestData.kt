package com.menta.bff.devices.login.entities.tags

import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag

val tagType = "EMV"
fun anAcquirerTagsEmv(): List<AcquirerTag> =
    listOf(
        AcquirerTag(
            acquirerId = "GPS",
            type = tagType,
            tags = listOf("9F26", "82", "9F36")
        )
    )
