plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.objectbox) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_function-naming" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_standard_package-name" to "disabled",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
    format("libs-versions") {
        target("gradle/libs.versions.toml")
    }
}

tasks.register("staticAnalysis") {
    group = "verification"
    description = "Run Spotless check, Detekt, and Android Lint"

    dependsOn("spotlessCheck")

    // Run Detekt on all modules that have it configured
    dependsOn(gradle.includedBuilds.map { it.task(":detekt") })
    dependsOn(subprojects.map { it.tasks.matching { t -> t.name == "detekt" } })

    // Run Android lint on the app module
    dependsOn(":composeApp:lint")
}
