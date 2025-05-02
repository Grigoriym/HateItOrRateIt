import com.grappim.hateitorrateit.AppBuildTypes
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.android.app)
    alias(libs.plugins.moduleGraphAssertion)
    alias(libs.plugins.compose)

    alias(libs.plugins.gms.googleServices) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

android {
    namespace = "com.grappim.hateitorrateit"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.grappim.hateitorrateit"
        testApplicationId = "com.grappim.hateitorrateit.tests"

        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 16
        versionName = "1.3.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging.resources.excludes.apply {
        add("META-INF/AL2.0")
        add("META-INF/LGPL2.1")
        add("META-INF/LICENSE.md")
        add("META-INF/LICENSE-notice.md")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.keystore.jks")
            keyAlias = System.getenv("HIOR_ALIAS_D")
            keyPassword = System.getenv("HIOR_KEY_PASS_D")
            storePassword = System.getenv("HIOR_STORE_PASS_D")
        }

        create("release") {
            storeFile = file("../release.keystore.jks")
            keyAlias = System.getenv("HIOR_ALIAS_R")
            keyPassword = System.getenv("HIOR_KEY_PASS_R")
            storePassword = System.getenv("HIOR_STORE_PASS_R")
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = AppBuildTypes.DEBUG.applicationIdSuffix

            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            applicationIdSuffix = AppBuildTypes.RELEASE.applicationIdSuffix

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true

        buildConfig = true
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    bundle {
        language {
            // Specifies that the app bundle should not support
            // configuration APKs for language resources. These
            // resources are instead packaged with each base and
            // dynamic feature APK.
            enableSplit = false
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
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

composeCompiler {
    enableStrongSkippingMode = true

    reportsDestination = layout.buildDirectory.dir("compose_compiler")
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
    implementation(projects.data.workerApi)
    implementation(projects.data.workerImpl)

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

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

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

    testImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test"))
    testImplementation(projects.testing.core)
    testImplementation(projects.testing.domain)
    androidTestImplementation(projects.testing.core)
    androidTestImplementation(projects.testing.domain)

    testImplementation(libs.robolectric)

    implementation(libs.coil)
    implementation(libs.timber)
}

moduleGraphAssert {
    maxHeight = 3
    assertOnAnyBuild = true
}
