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
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.jacocoAggregationResults)
    alias(libs.plugins.jacocoAggregationCoverage)
    alias(libs.plugins.gradleDoctor)
    alias(libs.plugins.dependencyAnalysis)
}

doctor {
    enableTestCaching.set(false)
    disallowCleanTaskDependencies.set(true)
    warnWhenJetifierEnabled.set(true)
    javaHome {
        ensureJavaHomeMatches.set(false)
        ensureJavaHomeIsSet.set(false)
        failOnError.set(false)
    }
}

allprojects {
    tasks.withType<Test> {
        // https://github.com/gradle/gradle/issues/33619#issuecomment-2913519014
        failOnNoDiscoveredTests = false
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
        plugin("dev.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("com.autonomousapps.dependency-analysis")
    }

    // https://github.com/cortinico/kotlin-android-template/
    detekt {
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        allRules = false
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.7.1")
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
    }

    dependencies {
        ktlintRuleset(rootProject.libs.composeRules.ktlint)
        detektPlugins(rootProject.libs.composeRules.detekt)
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
    "**/*_AppInfoProviderFactory",

    "**/*Screen",
    "**/*Activity",
    "**/*Screen*",
    "**/*Application",
    "**/*StateProvider",
    "**/*Widget",

    "**/*Plato*",
    "**/*Button*",

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

    "**/HioriTestRunner",

    "**/NoOp*",
    "**/AppInfoProviderImpl",
    "**/ResultExtension",
    "**/MainNavHost",
    "**/MainAppState",
    "**/TopBarController",
    "**/ObserveAsEvents",
    "**/BottomBar",
    "**/*NavDestination"
).flatMap {
    listOf(
        "$it.class",
        "${it}Kt.class",
        "$it\$*.class"
    )
}

testAggregation {
    modules {
        exclude(rootProject)
        exclude(projects.testing.core)
        exclude(projects.testing.domain)
        exclude(projects.uikit)
        exclude(projects.strings)
    }
    coverage {
        exclude(coverageExclusions)
    }
}
