package com.menta.api.terminals.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
import com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
import com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
import com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME

@AnalyzeClasses(packages = ["com.menta.api.terminals"], importOptions = [ImportOption.DoNotIncludeTests::class])
class CodingRulesTest {

    @ArchTest
    val exceptions_should_respect_naming_convention: ArchRule =
        classes()
            .that().resideInAPackage("..exception..")
            .should().haveSimpleNameEndingWith("Exception")
            .allowEmptyShould(true)

    @ArchTest
    var no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS

    @ArchTest
    var no_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS

    @ArchTest
    var no_java_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING

    @ArchTest
    var no_jodatime = NO_CLASSES_SHOULD_USE_JODATIME

    @ArchTest
    var classes_must_not_be_suffixed_with_impl: ArchRule =
        noClasses()
            .should().haveSimpleNameEndingWith("Impl")
            .because("En serio?!, y si nos esforzamos un poco más?")
}
