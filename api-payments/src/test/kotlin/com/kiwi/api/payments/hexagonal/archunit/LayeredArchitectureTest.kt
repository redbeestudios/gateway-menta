package com.kiwi.api.payments.hexagonal.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val SHARED = "Config"
private const val USECASE = "UseCase"
private const val PORTIN = "Port In"
private const val PORTOUT = "Port Out"

@AnalyzeClasses(packages = ["com.kiwi.api.payments"], importOptions = [ImportOption.DoNotIncludeTests::class])
class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(SHARED).definedBy("com.kiwi.api.payments.shared..")
        .layer(DOMAIN).definedBy("com.kiwi.api.payments.hexagonal.domain..")
        .layer(ADAPTERS).definedBy("com.kiwi.api.payments.hexagonal.adapter..")
        .layer(APPLICATION).definedBy("com.kiwi.api.payments.hexagonal.application..")
        .layer(USECASE).definedBy("com.kiwi.api.payments.hexagonal.application.usecase")
        .layer(PORTIN).definedBy("com.kiwi.api.payments.hexagonal.application.port.in")
        .layer(PORTOUT).definedBy("com.kiwi.api.payments.hexagonal.application.port.out")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, SHARED)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(SHARED, USECASE)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, SHARED)
        .whereLayer(USECASE).mayOnlyBeAccessedByLayers(PORTIN)
}
