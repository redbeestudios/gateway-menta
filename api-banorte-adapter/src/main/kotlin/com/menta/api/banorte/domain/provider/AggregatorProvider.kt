package com.menta.api.banorte.domain.provider

import com.menta.api.banorte.domain.Aggregator
import com.menta.api.banorte.domain.Aggregator.ChildCommerce
import com.menta.api.banorte.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AggregatorProvider(
    @Value("\${api-banorte-adapter.temporal-properties.aggregator.id}")
    private val aggregatorId: String,
    @Value("\${api-banorte-adapter.temporal-properties.aggregator.child_commerce.id}")
    private val childCommerceId: String
) {

    fun provide(): Aggregator =
        Aggregator(
            id = aggregatorId,
            childCommerce = ChildCommerce(
                id = childCommerceId,
            )
        ).log { info("aggregator provided: {}", it) }

    companion object : CompanionLogger()
}
