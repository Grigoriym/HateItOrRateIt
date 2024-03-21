import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.gms.googleServices) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)

    alias(libs.plugins.jacocoAggregationResults)
    alias(libs.plugins.jacocoAggregationCoverage)
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    detekt {
        parallel = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        allRules = false
    }

    ktlint {
        android = true
        ignoreFailures = false
        reporters {
            reporter(ReporterType.HTML)
        }
    }

    tasks.withType<Test> {
        failFast = true
        reports {
            html.required.set(true)
        }
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            showStandardStreams = true
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
        }
    }
}

private val coverageExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",

    "**/*Module*.*",
    "**/*Module",
    "**/*Dagger*.*",
    "**/*Hilt*.*",
    "**/*GeneratedInjector",
    "**/*HiltComponents*",
    "**/*_HiltModules*",
    "**/*_Provide*",
    "**/*_Factory*",
    "**/*_ComponentTreeDeps",
    "**/*_Impl*",
    "**/*DefaultImpls*",

    "**/*Screen",
    "**/*Activity",
    "**/*Screen*",
    "**/*Application",
    "**/*StateProvider",

    "**/*Plato*",
    "**/*Button*",
    "**/TextH*",
    "**/*Texts*",
    "**/Theme",
    "**/Colors",
    "**/*HateItOrRateItTheme*",
    "**/TypeKt",

    "**/LocalDataStorageImpl",
    "**/TransactionControllerImpl",
    "**/AnalyticsControllerImpl",
    "**/*HateItOrRateItDatabase",
    "**/*LoggerInitializer",
    "**/*DevelopmentTree",
    "**/*ProductionTree",
    "**/*RootNavDestinations",
    "**/*HomeNavDestination",
    "**/*HashUtils",
    "**/*NavUtils",
    "**/DebugAnalyticsControllerImpl",
    "**/RemoteConfigsListenerImpl",
    "**/RemoteConfigManagerImpl"
).flatMap {
    listOf(
        "$it.class",
        "${it}Kt.class",
        "$it\$*.class"
    )
}

testAggregation {
    coverage {
        exclude(coverageExclusions)
    }
}

val runAllUnitTests by tasks.creating {
    group = "verification"
    description = "Runs all unit tests in all modules."

    dependsOn(subprojects.map { it.tasks.withType<Test>() })
}

val runAndroidTests by tasks.creating {
    group = "verification"
    description = "Runs Android instrumented tests only in :app and data:db modules."

    dependsOn(":app:connectedAndroidTest", ":data:db:connectedAndroidTest")
}
