package com.menta.api.credibanco.adapter.db

import com.menta.api.credibanco.adapter.db.entity.ResponseOperation
import org.springframework.data.jpa.repository.JpaRepository

interface OperationDbRepository : JpaRepository<ResponseOperation, Int>
