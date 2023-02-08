package com.menta.api.feenicia.adapter.controller.provider

import com.menta.api.feenicia.domain.Operation.Items
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ItemsProvider(
    @Value("\${api-feenicia-adapter.properties.description}")
    private val description: String
) {

    fun provide(amount: Double): List<Items> =
        listOf(
            Items(
                id = 0,
                amount = amount,
                description = description,
                quantity = 1,
                unitPrice = amount.toString()
            )
        )

    companion object : CompanionLogger()
}
