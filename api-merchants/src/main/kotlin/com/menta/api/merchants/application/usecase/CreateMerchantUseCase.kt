package com.menta.api.merchants.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.application.port.`in`.CreateMerchantPortIn
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.LegalTypeValidator
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.PreMerchant
import com.menta.api.merchants.domain.Status
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.domain.provider.IdProvider
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.merchantExists
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreateMerchantUseCase(
    private val merchantRepository: MerchantRepositoryOutPort,
    private val idProvider: IdProvider,
    private val dateProvider: DateProvider,
    private val validator: LegalTypeValidator
) : CreateMerchantPortIn {

    override fun execute(
        preMerchant: PreMerchant,
        existingMerchant: Optional<Merchant>
    ): Either<ApplicationError, Merchant> =
        existingMerchant.shouldNotExist().flatMap {
            preMerchant
                .validateLegalType().map {
                    it.buildMerchant()
                        .save()
                        .log { info("Merchant {} created with id {}", it.businessName, it.id) }
                }
        }

    private fun Optional<Merchant>.shouldNotExist() =
        if (this.isEmpty) {
            Unit.right()
        } else {
            merchantExists().left()
        }

    private fun Merchant.save() = merchantRepository.create(this)

    private fun PreMerchant.validateLegalType() =
        validator.validate(this)
            .logRight { info("Legal Type Validated") }

    private fun PreMerchant.buildMerchant(): Merchant =
        Merchant(
            id = idProvider.provide(),
            customerId = customerId,
            country = country,
            legalType = legalType,
            businessName = businessName,
            fantasyName = fantasyName,
            representative = representative?.let {
                Merchant.Representative(
                    representativeId = Merchant.Representative.RepresentativeId(
                        type = representative.representativeId.type,
                        number = representative.representativeId.number
                    ),
                    birthDate = representative.birthDate,
                    name = representative.name,
                    surname = representative.surname
                )
            },
            businessOwner = businessOwner?.let {
                Merchant.BusinessOwner(
                    birthDate = businessOwner.birthDate,
                    name = businessOwner.name,
                    surname = businessOwner.surname,
                    ownerId = Merchant.BusinessOwner.OwnerId(
                        type = businessOwner.ownerId.type,
                        number = businessOwner.ownerId.number
                    )
                )
            },
            merchantCode = merchantCode,
            address = Merchant.Address(
                address.state,
                address.city,
                address.zip,
                address.street,
                address.number,
                address.floor,
                address.apartment
            ),
            email = email,
            phone = phone,
            activity = activity,
            category = category,
            tax = Merchant.Tax(
                id = tax.id,
                type = tax.type
            ),
            settlementCondition = Merchant.SettlementCondition(
                settlementCondition.transactionFee,
                settlementCondition.settlement,
                settlementCondition.cbuOrCvu
            ),
            status = Status.ACTIVE,
            createDate = dateProvider.provide(),
            updateDate = dateProvider.provide(),
            deleteDate = null
        )
            .log { info("Merchant Created: {}", it) }

    companion object : CompanionLogger()
}
