package com.kiwi.api.reimbursements.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val SHARED = "Shared"

class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(SHARED).definedBy("com.kiwi.api.reimbursements.hexagonal.shared..")
        .layer(DOMAIN).definedBy("com.kiwi.api.reimbursements.hexagonal.domain..")
        .layer(ADAPTERS).definedBy("com.kiwi.api.reimbursements.hexagonal.adapter..")
        .layer(APPLICATION).definedBy("com.kiwi.api.reimbursements.hexagonal.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, SHARED)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(SHARED)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, SHARED)
}
