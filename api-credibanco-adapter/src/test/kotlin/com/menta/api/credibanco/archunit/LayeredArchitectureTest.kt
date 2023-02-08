package com.menta.api.credibanco.archunit

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
        .layer(SHARED).definedBy("com.menta.api.seeds.shared..")
        .layer(DOMAIN).definedBy("com.menta.api.seeds.domain..")
        .layer(ADAPTERS).definedBy("com.menta.api.seeds.adapter..")
        .layer(APPLICATION).definedBy("com.menta.api.seeds.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, SHARED)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(SHARED)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, SHARED)
}
