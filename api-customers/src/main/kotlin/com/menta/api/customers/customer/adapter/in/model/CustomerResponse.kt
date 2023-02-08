package com.menta.api.customers.customer.adapter.`in`.model

import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.LegalType
import com.menta.api.customers.customer.domain.Status
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.OffsetDateTime
import java.util.UUID

@Relation(collectionRelation = "customers")
class CustomerResponse(
    val id: UUID,
    val country: Country,
    val legalType: LegalType,
    val businessName: String,
    val fantasyName: String,
    val tax: Tax,
    val activity: String,
    val email: String,
    val phone: String,
    val address: Address,
    val representative: Representative?,
    val businessOwner: BusinessOwner?,
    val settlementCondition: SettlementCondition?,
    val status: Status,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime,
    val deleteDate: OffsetDateTime?
) : RepresentationModel<CustomerResponse>() {
    data class Tax(
        val type: String,
        val id: String
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

    data class SettlementCondition(
        val transactionFee: String,
        val settlement: String,
        val cbuOrCvu: String
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CustomerResponse

        if (id != other.id) return false
        if (country != other.country) return false
        if (legalType != other.legalType) return false
        if (businessName != other.businessName) return false
        if (fantasyName != other.fantasyName) return false
        if (tax != other.tax) return false
        if (activity != other.activity) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (address != other.address) return false
        if (representative != other.representative) return false
        if (businessOwner != other.businessOwner) return false
        if (settlementCondition != other.settlementCondition) return false
        if (status != other.status) return false
        if (deleteDate != other.deleteDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + legalType.hashCode()
        result = 31 * result + businessName.hashCode()
        result = 31 * result + fantasyName.hashCode()
        result = 31 * result + tax.hashCode()
        result = 31 * result + activity.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (representative?.hashCode() ?: 0)
        result = 31 * result + (businessOwner?.hashCode() ?: 0)
        result = 31 * result + (settlementCondition?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + (deleteDate?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CustomerResponse(id='$id', country='$country', legalType='$legalType', businessName='$businessName', fantasyName='$fantasyName', tax=$tax, activity='$activity', email='$email', phone='$phone', address=$address, representative=$representative, businessOwner=$businessOwner, settlementCondition=$settlementCondition, status='$status', deleteDate=$deleteDate)"
    }
}
