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

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    // https://github.com/cortinico/kotlin-android-template/
    detekt {
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        allRules = false
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(false)
        verbose.set(true)
        outputColorName.set("RED")
        outputToConsole.set(true)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
            reporter(ReporterType.JSON)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
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
