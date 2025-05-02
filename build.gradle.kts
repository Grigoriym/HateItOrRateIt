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
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)

    alias(libs.plugins.jacocoAggregationResults)
    alias(libs.plugins.jacocoAggregationCoverage)

    alias(libs.plugins.gradleDoctor)
    alias(libs.plugins.dependencyAnalysis)
}

doctor {
    failOnEmptyDirectories.set(false)
    enableTestCaching.set(false)
    failOnEmptyDirectories.set(true)
    warnWhenNotUsingParallelGC.set(true)
    disallowCleanTaskDependencies.set(true)
    warnWhenJetifierEnabled.set(true)
    javaHome {
        failOnError.set(false)
    }
}

allprojects {
    tasks.withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
        }
    }
}

val runAndroidTests by tasks.creating {
    group = "verification"
    description = "Runs Android instrumented tests only in specific modules"

    dependsOn(
        ":app:connectedAndroidTest",
        ":data:db:connectedAndroidTest",
        ":feature:settings:ui:connectedAndroidTest",
        ":data:local-datastorage-impl:connectedAndroidTest"
    )
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
    "**/*AnalyticsControllerImpl",
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
    "**/WorkerControllerImpl",

    "**/TestUtils",
    "**/HioriTestRunner",

    "**/NoOp*",
    "**/AppInfoProviderImpl"
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
