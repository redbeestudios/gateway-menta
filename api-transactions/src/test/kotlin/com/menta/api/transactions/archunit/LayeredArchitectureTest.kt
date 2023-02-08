package com.menta.api.transactions.archunit

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
        .layer(SHARED).definedBy("com.menta.api.transactions.shared..")
        .layer(DOMAIN).definedBy("com.menta.api.transactions.domain..")
        .layer(ADAPTERS).definedBy("com.menta.api.transactions.adapter..")
        .layer(APPLICATION).definedBy("com.menta.api.transactions.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, SHARED)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(SHARED)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, SHARED)
}
