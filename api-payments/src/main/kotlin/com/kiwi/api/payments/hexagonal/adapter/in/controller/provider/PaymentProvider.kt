package com.kiwi.api.payments.hexagonal.adapter.`in`.controller.provider

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.BillPaymentController.Companion.log
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentRequest
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.payments.hexagonal.domain.Payment
import com.kiwi.api.payments.hexagonal.domain.Terminal
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PaymentProvider(
    private val toPaymentMapper: ToPaymentMapper,
    private val findCustomerPortIn: FindCustomerPortIn,
    private val findTerminalPortIn: FindTerminalPortIn,
    private val findMerchantPortIn: FindMerchantPortIn
) {

    fun provide(paymentRequest: PaymentRequest): Payment =
        findTerminal(paymentRequest.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toPaymentMapper.map(paymentRequest, merchant.await(), customer.await(), it).log {
                    info("payment mapped for request")
                }
            }
        }

    private fun findTerminal(terminalId: UUID) =
        findTerminalPortIn.execute(terminalId)

    suspend fun Terminal.findCustomer() =
        findCustomerPortIn.execute(customerId)

    suspend fun Terminal.findMerchant() =
        findMerchantPortIn.execute(merchantId)
}
