package com.menta.bff.devices.login.entities.deviceFlow

import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import java.util.UUID

val DEVICE_FLOW_ID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

fun anDevicesFlows(): List<DeviceFlow> =
    listOf(
        DeviceFlow(
            id = DEVICE_FLOW_ID,
            field = "CVV",
            uri = "http://www.menta.com/cvv",
            model = "I2000",
            flowType = "REFUND",
            order = 1,
            validate = DeviceFlow.Validate(
                rule = "AND",
                validations = listOf(
                    DeviceFlow.Validation(
                        field = "PAYMENT_METHOD_SELECTED",
                        rule = "EQUALS",
                        value = "DEBIT"
                    ),
                    DeviceFlow.Validation(
                        field = "TRANSACTION_TYPE",
                        rule = "EQUALS",
                        value = "BILL"
                    )
                )
            )
        )
    )
