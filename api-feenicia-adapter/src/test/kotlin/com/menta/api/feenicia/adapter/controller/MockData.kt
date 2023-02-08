package com.menta.api.feenicia.adapter.controller

import com.menta.api.feenicia.shared.error.ErrorHandler
import com.menta.api.feenicia.shared.error.providers.CurrentResourceProvider
import com.menta.api.feenicia.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import com.menta.api.feenicia.utils.TestConstants
import javax.servlet.http.HttpServletRequest

fun aJsonRequest() = """
{
   "capture": {
      "card": {
         "holder": {
            "name": "${TestConstants.HOLDER_NAME}"
         },
         "pan": "${TestConstants.CARD_PAN}",
         "expiration_date": "${TestConstants.CARD_EXPIRATION_DATE}",
         "cvv": "${TestConstants.CARD_CVV}",
         "brand": "${TestConstants.CARD_BRAND.name}",
         "type": "${TestConstants.CARD_TYPE}",
         "bank": "${TestConstants.CARD_BANK}",
         "track1": "${TestConstants.CARD_TRACK1}",
         "track2": "${TestConstants.CARD_TRACK2}",
         "pin": "${TestConstants.CARD_PIN}",
         "emv": {
              "ksn": "${TestConstants.CARD_KSN}",
              "icc_data": "${TestConstants.CARD_ICC_DATA}"
         }
      },
      "input_mode": "${TestConstants.INPUT_MODE}"
   },
   "amount": {
      "total": "${TestConstants.AMOUNT}",
      "currency": "${TestConstants.CURRENCY}",
      "breakdown": [
         {
            "description": "${TestConstants.DESCRIPTION_BREAKDOWN}",
            "amount": "${TestConstants.AMOUNT}"
         }
      ]
   },
   "datetime": "${TestConstants.DATE_TIME}",
   "trace": "${TestConstants.TRANSACTION_TRACE}",
   "ticket": "${TestConstants.TRANSACTION_TICKET}",
   "terminal": {
      "id": "${TestConstants.TERMINAL_ID}",
      "serial_code": "${TestConstants.TERMINAL_SERIAL_CODE}",
      "software_version": "${TestConstants.TERMINAL_SOFTWARE_VERSION}",
      "features": [
         "CHIP"
      ]
   },
   "merchant": {
       "id": "${TestConstants.MERCHANT_ID}"
   },
   "batch": "${TestConstants.TRANSACTION_BATCH}",
   "installments": "${TestConstants.INSTALLMENTS}",
   "retrieval_reference_number": "${TestConstants.RRN}",
   "customer": {
        "id": "${TestConstants.CUSTOMER_ID}"
   }
}
""".trimIndent()

fun anInvalidRequest() = aJsonRequest().replace("\"currency\": \"ARS\",", "")

fun aControllerAdvice(request: HttpServletRequest) =
    ErrorHandler(
        errorResponseProvider = ErrorResponseProvider(
            currentResourceProvider = CurrentResourceProvider(request),
            metadataProvider = ErrorResponseMetadataProvider(
                currentResourceProvider = CurrentResourceProvider(request)
            )
        )
    )
