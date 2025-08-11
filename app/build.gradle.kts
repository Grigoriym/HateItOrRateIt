plugins {
    alias(libs.plugins.hateitorrateit.android.app)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose)

    alias(libs.plugins.moduleGraphAssertion)

    alias(libs.plugins.gms.googleServices) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

android {
    namespace = "com.grappim.hateitorrateit"

    defaultConfig {
        applicationId = "com.grappim.hateitorrateit"
        testApplicationId = "com.grappim.hateitorrateit.tests"

        versionCode = 28
        versionName = "1.4.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

// It will find a gplay build only if start building specifically gplay build
val isGooglePlayBuild = project.gradle.startParameter.taskRequests.toString().contains("Gplay")

logger.lifecycle("${project.gradle.startParameter.taskRequests}")
project.gradle.startParameter.taskRequests.forEach {
    logger.lifecycle("🔥 Detected Gradle Task: $it")
}

if (isGooglePlayBuild) {
    logger.lifecycle("✅ Applying Google Services Plugin due to detected Google Play Build!")
    apply(plugin = libs.plugins.gms.googleServices.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
} else {
    logger.lifecycle("🚫 Google Services Plugin is NOT applied for this variant.")
    // Disable DependencyInfoBlock for fdroid builds
    android {
        dependenciesInfo {
            includeInApk = false
            includeInBundle = false
        }
    }
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.uikit)

    implementation(projects.utils.ui)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.utils.dateTime)
    implementation(projects.utils.filesApi)
    implementation(projects.utils.filesImpl)
    implementation(projects.utils.androidApi)
    implementation(projects.utils.androidImpl)

    implementation(projects.data.analyticsApi)
    implementation(projects.data.analyticsImpl)

    implementation(projects.core.async)
    implementation(projects.core.appinfoApi)
    implementation(projects.core.appUpdateApi)
    implementation(projects.core.appUpdateImpl)

    implementation(projects.data.db)
    implementation(projects.data.repoApi)
    implementation(projects.data.repoImpl)
    implementation(projects.data.cleanerApi)
    implementation(projects.data.cleanerImpl)
    implementation(projects.data.remoteConfigApi)
    implementation(projects.data.remoteConfigImpl)
    implementation(projects.data.localDatastorageApi)
    implementation(projects.data.localDatastorageImpl)

    implementation(projects.feature.home.ui)
    implementation(projects.feature.productmanager.ui)
    implementation(projects.feature.details.ui)
    implementation(projects.feature.settings.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    gplayImplementation(platform(libs.firebase.bom))
    gplayImplementation(libs.firebase.crashlytics)
    gplayImplementation(libs.firebase.analytics)

    implementation(libs.google.material)

    testImplementation(libs.robolectric)

    implementation(libs.coil)
    implementation(libs.timber)
}

moduleGraphAssert {
    maxHeight = 3
    assertOnAnyBuild = true
}
