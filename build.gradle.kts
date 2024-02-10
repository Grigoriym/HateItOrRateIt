plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.gms.googleServices) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        parallel = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        allRules = false
    }
}

val runAllUnitTests by tasks.creating {
    group = "verification"
    description = "Runs all unit tests in all modules."
    dependsOn(subprojects.map { it.tasks.withType<Test>() })
}

val runAllAndroidTests by tasks.creating {
    group = "verification"
    description = "Runs all Android instrumented tests in all modules."
    dependsOn(subprojects.flatMap {
        it.tasks.matching { task -> task.name.startsWith("connected") && task.name.endsWith("AndroidTest") }
    })
}