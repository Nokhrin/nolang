package com.nokhrin.nolang;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private static final JavaClasses CLASSES =
        new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.nokhrin.nolang");

    @Test
    void operationsDoNotDependOnCombinatorsOrAlgebraic() {
        noClasses()
            .that().resideInAPackage("..common.operations..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..common.combinators..", "..algebraic..")
            .check(CLASSES);
    }

    @Test
    void layering() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Values")
            .definedBy("com.nokhrin.nolang.common.values..")
            .layer("Core")
            .definedBy("com.nokhrin.nolang.common.core..")
            .layer("Operations")
            .definedBy("com.nokhrin.nolang.common.operations..")
            .layer("Combinators")
            .definedBy("com.nokhrin.nolang.common.combinators..")
            .layer("Algebraic")
            .definedBy("com.nokhrin.nolang.algebraic..")
            .layer("Generated")
            .definedBy("com.nokhrin.nolang")
            .whereLayer("Core")
            .mayOnlyAccessLayers("Values", "Combinators")
            .whereLayer("Operations")
            .mayOnlyAccessLayers("Values", "Core")
            .whereLayer("Combinators")
            .mayOnlyAccessLayers("Values", "Core", "Operations")
            .whereLayer("Algebraic")
            .mayOnlyAccessLayers("Values", "Core", "Operations", "Combinators", "Generated")
            .whereLayer("Generated")
            .mayOnlyAccessLayers("Values", "Core", "Operations", "Combinators", "Algebraic")
            .check(CLASSES);
    }
}
