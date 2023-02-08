package com.menta.api.credibanco.adapter.db.entity

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "response_operations")
data class ResponseOperation(
    @Id
    @Column(name = "id")
    @Type(type = "uuid-char")
    val id: UUID,
    @Column(name = "retrieval_reference_number")
    val retrievalReferenceNumber: String?,
    @Column(name = "authorization_code")
    val authorizationCode: String?,
    @Column(name = "response_code")
    val responseCode: String?,
    @Column(name = "card_type_response_code")
    val cardTypeResponseCode: String,
    @Column(name = "receiving_institution_idenfication_code")
    val receivingInstitutionIdenficationCode: String,
    @Column(name = "settlement_data")
    val settlementData: String?
)
