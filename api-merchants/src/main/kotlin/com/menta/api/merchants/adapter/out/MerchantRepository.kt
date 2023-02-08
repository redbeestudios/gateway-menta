package com.menta.api.merchants.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.MerchantQuery
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.utils.logs.CompanionLogger
import com.menta.api.merchants.shared.utils.logs.benchmark
import com.menta.api.merchants.shared.utils.logs.exception
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class MerchantRepository(
    private val merchantDbRepository: MerchantDbRepository,
    private val mongoTemplate: MongoTemplate,
    private val dateProvider: DateProvider
) : MerchantRepositoryOutPort {

    override fun findBy(merchantId: UUID): Optional<Merchant> =
        log.benchmark("find merchant by id") {
            merchantDbRepository.findByIdAndDeleteDateIsNull(merchantId)
        }.log { info("merchant found: {}", it) }

    override fun findBy(taxType: String, taxId: String): Optional<Merchant> =
        log.benchmark("find merchant by tax") {
            merchantDbRepository.findFirstByTaxIdAndTaxTypeAndDeleteDateIsNull(taxId, taxType)
        }.log { info("merchant found: {}", it) }

    override fun create(merchant: Merchant): Merchant =
        merchantDbRepository.insert(merchant)

    override fun update(merchant: Merchant): Either<ApplicationError, Merchant> =
        try {
            log.benchmark("update merchant") {
                merchantDbRepository.save(merchant).right()
            }.log { info("merchant updated: {}", it) }
        } catch (ex: Exception) {
            ApplicationError.serverError(ex).left()
                .log { exception(ex) }
        }

    override fun findBy(merchantQuery: MerchantQuery, pageable: Pageable): Page<Merchant> =
        log.benchmark("Find merchant entity by filter") {
            getCriteria(merchantQuery)
                .toQuery()
                .getPage(pageable, Merchant::class.java)
        }

    private fun getCriteria(
        merchantQuery: MerchantQuery
    ) =
        with(merchantQuery) {
            Criteria()
                .apply { and("deleteDate").`is`(null) }
                .apply { merchantId?.let { and("id").`is`(it) } }
                .apply { status?.let { and("status").`is`(it.name) } }
                .apply { customerId?.let { and("customerId").`is`(it) } }
                .apply { createDate?.let {
                    andOperator(
                        Criteria.where("createDate").gte(dateProvider.provideFromDate(it)),
                        Criteria.where("createDate").lte(dateProvider.provideToDate(it)),
                    )
                } }
        }

    private fun Criteria.toQuery() = Query(this)

    private fun <T> Query.getPage(pageable: Pageable, entityClass: Class<T>): Page<T> = let { query ->
        (
            mongoTemplate.count(query, "merchants")
                to
                    query
                        .with(pageable)
                        .let { mongoTemplate.find(query, entityClass) }
            )
            .let { result ->
                PageImpl(result.second, pageable, result.first)
            }
    }
    companion object : CompanionLogger()
}
