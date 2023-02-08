package com.menta.apisecrets

import com.menta.apisecrets.adapter.out.event.Header
import com.menta.apisecrets.adapter.out.event.TerminalUpdateProducer.Companion.HEADER
import com.menta.apisecrets.adapter.out.model.CustomerResponse
import com.menta.apisecrets.adapter.out.model.TerminalResponse
import com.menta.apisecrets.domain.Acquirer.GPS
import com.menta.apisecrets.domain.Country
import com.menta.apisecrets.domain.Customer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.domain.Terminal
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.OffsetDateTime
import java.util.UUID

fun aCustomer() =
    Customer(
        id = UUID.randomUUID(),
        country = Country.ARG
    )

fun aCustomerResponse(country: String = "ARG") =
    CustomerResponse(
        id = UUID.randomUUID(),
        country = country,
        legalType = "NATURAL_PERSON",
        businessName = "a business name",
        fantasyName = "a fantasy name",
        tax = CustomerResponse.Tax(
            type = "a tax type",
            id = "a tax id"
        ),
        activity = "an activity",
        email = "hola@hola.com",
        phone = "1234567890",
        address = CustomerResponse.Address(
            state = "as tate",
            city = "a city",
            zip = "a zip",
            street = "a street",
            number = "a number",
            floor = "a floor",
            apartment = "an apartment"
        ),
        representative = CustomerResponse.Representative(
            representativeId = CustomerResponse.Representative.RepresentativeId(
                type = "DNI",
                number = "99999999"
            ),
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            name = "Jose",
            surname = "Perez"
        ),
        businessOwner = CustomerResponse.BusinessOwner(
            name = "Pedro",
            surname = "Gonzalez",
            birthDate = OffsetDateTime.parse("2022-03-31T22:01:01.999+07:00"),
            ownerId = CustomerResponse.BusinessOwner.OwnerId(
                type = "DNI",
                number = "99888777"
            )
        ),
        settlementCondition = CustomerResponse.SettlementCondition(
            transactionFee = "transactionFee",
            settlement = "settlement",
            cbuOrCvu = "cbuOrCvu"
        ),
        status = "a status"
    )

fun aTerminal(serialCode: String = "aSerialCode") =
    Terminal(
        serialCode = serialCode,
        id = UUID.randomUUID(),
        merchant = Terminal.Merchant(
            id = UUID.randomUUID(),
            customer = Terminal.Merchant.Customer(
                id = UUID.randomUUID()
            )
        )
    )

fun aTerminalResponse(serialCode: String = "aSerialCode") =
    TerminalResponse(
        id = UUID.randomUUID(),
        merchantId = UUID.randomUUID(),
        customerId = UUID.randomUUID(),
        serialCode = serialCode,
        hardwareVersion = "a hardware version",
        tradeMark = "a trademark",
        model = "a model",
        status = TerminalResponse.Status.ACTIVE,
        features = listOf(TerminalResponse.Feature.CHIP)
    )

fun aSecret() =
    Secret(
        master = "a master",
        ksn = "a ksn"
    )

val aSecrets =
    Secrets(
        options = listOf(aSecret()),
        context = Secrets.Context(
            customer = aCustomer(),
            terminal = aTerminal(),
            acquirer = GPS
        )
    )

const val aTopic = "a topic"
const val aSerializedMessage = "a serialized message"

val aProducerRecord =
    with(aSecrets.context) {
        ProducerRecord(
            aTopic,
            null,
            "${terminal.id}",
            aSerializedMessage,
            listOf(
                Header(
                    HEADER, acquirer.name
                )
            )
        )
    }
