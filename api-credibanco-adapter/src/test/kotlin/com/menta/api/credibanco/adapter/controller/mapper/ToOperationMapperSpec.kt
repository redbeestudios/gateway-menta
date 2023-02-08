package com.menta.api.credibanco.adapter.controller.mapper

import com.menta.api.credibanco.aConstant
import com.menta.api.credibanco.aCredibancoMerchant
import com.menta.api.credibanco.aCredibancoTerminal
import com.menta.api.credibanco.aRequestPayment
import com.menta.api.credibanco.aSettlementData
import com.menta.api.credibanco.aTerminalType
import com.menta.api.credibanco.anOperation
import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.domain.field.provider.CardTypeProvider
import com.menta.api.credibanco.domain.field.provider.ConstantsProvider
import com.menta.api.credibanco.domain.field.provider.MtiProvider
import com.menta.api.credibanco.domain.field.provider.ProcessCodeProvider
import com.menta.api.credibanco.domain.field.provider.SettlementDataProvider
import com.menta.api.credibanco.domain.field.provider.TerminalDataProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOperationMapperSpec : FeatureSpec({
    feature("map request") {

        beforeEach { clearAllMocks() }

        val constantsProvider = ConstantsProvider(aConstant())

        val mapper = ToOperationMapper(
            mtiProvider = MtiProvider(),
            processCodeProvider = ProcessCodeProvider(),
            constantsProvider = constantsProvider,
            terminalDataProvider = TerminalDataProvider(constantsProvider),
            cardTypeProvider = CardTypeProvider(aTerminalType()),
            settlementDataProvider = SettlementDataProvider(aSettlementData())
        )

        scenario("successful operation mapping for PURCHASE") {
            mapper.map(aRequestPayment(), OperationType.PURCHASE, aCredibancoTerminal(), aCredibancoMerchant()) shouldBe anOperation
        }
    }
})
