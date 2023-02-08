package com.menta.api.merchants.adapter.`in`.model

import com.menta.api.merchants.domain.Country
import com.menta.api.merchants.domain.LegalType
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.OffsetDateTime
import java.util.UUID

@Relation(collectionRelation = "merchants")
class MerchantResponse(
    val id: UUID,
    val customerId: String,
    val country: Country,
    val legalType: LegalType,
    val businessName: String?,
    val fantasyName: String?,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val merchantCode: String,
    val address: Address,
    val email: String,
    val phone: String,
    val activity: String,
    val category: String,
    val tax: Tax,
    val settlementCondition: SettlementCondition,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime?,
    val deleteDate: OffsetDateTime?
) : RepresentationModel<MerchantResponse>() {
    data class Representative(
        val representativeId: RepresentativeId,
        val birthDate: OffsetDateTime,
        val name: String,
        val surname: String
    ) {
        data class RepresentativeId(
            val type: String,
            val number: String
        )
    }

    data class BusinessOwner(
        val name: String,
        val surname: String,
        val birthDate: OffsetDateTime,
        val ownerId: OwnerId
    ) {
        data class OwnerId(
            val type: String,
            val number: String
        )
    }

    data class SettlementCondition(
        val transactionFee: String,
        val settlement: String,
        val cbuOrCvu: String
    )

    data class Address(
        val state: String,
        val city: String,
        val zip: String,
        val street: String,
        val number: String,
        val floor: String?,
        val apartment: String?
    )

    data class Tax(
        val id: String,
        val type: String
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MerchantResponse

        if (id != other.id) return false
        if (customerId != other.customerId) return false
        if (country != other.country) return false
        if (legalType != other.legalType) return false
        if (businessName != other.businessName) return false
        if (fantasyName != other.fantasyName) return false
        if (representative != other.representative) return false
        if (businessOwner != other.businessOwner) return false
        if (merchantCode != other.merchantCode) return false
        if (address != other.address) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (activity != other.activity) return false
        if (category != other.category) return false
        if (tax != other.tax) return false
        if (settlementCondition != other.settlementCondition) return false
        if (deleteDate != other.deleteDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + customerId.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + legalType.hashCode()
        result = 31 * result + (businessName?.hashCode() ?: 0)
        result = 31 * result + (fantasyName?.hashCode() ?: 0)
        result = 31 * result + (representative?.hashCode() ?: 0)
        result = 31 * result + (businessOwner?.hashCode() ?: 0)
        result = 31 * result + merchantCode.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + activity.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + tax.hashCode()
        result = 31 * result + settlementCondition.hashCode()
        result = 31 * result + (deleteDate?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MerchantResponse(id=$id, customerId='$customerId', country=$country, legalType=$legalType, businessName=$businessName, fantasyName=$fantasyName, representative=$representative, businessOwner=$businessOwner, merchantCode='$merchantCode', address=$address, email='$email', phone='$phone', activity='$activity', category='$category', tax=$tax, settlementCondition=$settlementCondition, createDate=$createDate, updateDate=$updateDate, deleteDate=$deleteDate)"
    }
}
