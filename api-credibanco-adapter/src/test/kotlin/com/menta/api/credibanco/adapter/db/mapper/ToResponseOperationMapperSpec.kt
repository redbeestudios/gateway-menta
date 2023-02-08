package com.menta.api.credibanco.adapter.db.mapper

import com.menta.api.credibanco.TestConstants.Companion.OPERATION_RESPONSE_ID
import com.menta.api.credibanco.aCreatedOperation
import com.menta.api.credibanco.aResponseOperation
import com.menta.api.credibanco.adapter.controller.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class ToResponseOperationMapperSpec : FeatureSpec({
    val idProvider: IdProvider = mockk()
    val mapper = ToResponseOperationMapper(idProvider)

    feature("to response operation mapper") {
        scenario("successful mapping") {
            every { idProvider.provide() } returns UUID.fromString(OPERATION_RESPONSE_ID)
            mapper.map(aCreatedOperation()) shouldBe aResponseOperation()
        }
    }
})
