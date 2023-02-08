package com.menta.api.customers.customer.adapter.`in`.model.hateos

import com.menta.api.customers.customer.adapter.`in`.model.CustomerResponse
import com.menta.api.customers.customer.domain.Customer
import com.menta.api.customers.customer.domain.provider.AppUrlProvider
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class CustomerModelAssembler(
    private val appUrlProvider: AppUrlProvider
) : RepresentationModelAssembler<Customer, CustomerResponse> {
    override fun toModel(customer: Customer): CustomerResponse =
        with(customer) {
            CustomerResponse(
                id = id,
                country = country,
                legalType = legalType,
                businessName = businessName,
                fantasyName = fantasyName,
                tax = CustomerResponse.Tax(tax.type, tax.id),
                activity = activity,
                email = email,
                phone = phone,
                address = CustomerResponse.Address(
                    address.state,
                    address.city,
                    address.zip,
                    address.street,
                    address.number,
                    address.floor,
                    address.apartment
                ),
                businessOwner = businessOwner?.let {
                    CustomerResponse.BusinessOwner(
                        name = businessOwner.name,
                        surname = businessOwner.surname,
                        birthDate = businessOwner.birthDate,
                        ownerId = CustomerResponse.BusinessOwner.OwnerId(
                            type = businessOwner.ownerId.type,
                            number = businessOwner.ownerId.number
                        )
                    )
                },
                representative = representative?.let {
                    CustomerResponse.Representative(
                        representativeId = CustomerResponse.Representative.RepresentativeId(
                            type = representative.representativeId.type,
                            number = representative.representativeId.number
                        ),
                        birthDate = representative.birthDate,
                        name = representative.name,
                        surname = representative.surname
                    )
                },
                settlementCondition = settlementCondition?.let {
                    CustomerResponse.SettlementCondition(
                        settlementCondition.transactionFee,
                        settlementCondition.settlement,
                        settlementCondition.cbuOrCvu
                    )
                },
                status = status,
                createDate = createDate,
                updateDate = updateDate,
                deleteDate = deleteDate
            ).add(
                Link.of(
                    "${appUrlProvider.provide()}/$id"
                ).withSelfRel()
            )
        }
}
