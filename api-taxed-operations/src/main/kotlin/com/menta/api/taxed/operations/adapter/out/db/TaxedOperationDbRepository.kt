package com.menta.api.taxed.operations.adapter.out.db

import com.menta.api.taxed.operations.adapter.out.db.entity.TaxedOperation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaxedOperationDbRepository : JpaRepository<TaxedOperation, UUID>
