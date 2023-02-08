package com.kiwi.api.reimbursements.hexagonal.adapter.out.db

import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.entity.Operation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OperationDbRepository : JpaRepository<Operation, UUID>
