package com.menta.bff.devices.login.entities.tags.domain

data class AcquirerTag(
    val acquirerId: String,
    val type: String,
    val tags: List<String>
)
