import com.grappim.hateitorrateit.HateItOrRateItBuildTypes

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.gms.googleServices)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.grappim.hateitorrateit"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

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
            applicationIdSuffix = HateItOrRateItBuildTypes.DEBUG.applicationIdSuffix

            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            applicationIdSuffix = HateItOrRateItBuildTypes.RELEASE.applicationIdSuffix

            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "17"

        freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
    buildFeatures {
        compose = true

        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeKotlinCompiler.get()
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":ui"))
    implementation(project(":domain"))
    implementation(project(":utils"))
    implementation(project(":data:db"))
    implementation(project(":data:worker-api"))
    implementation(project(":data:repo-api"))
    implementation(project(":data:cleaner-api"))
    implementation(project(":data:local-datastorage-api"))
    implementation(project(":di"))
    implementation(project(":commons"))
    implementation(project(":analytics-api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.timber)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.runtime.compose)

    implementation(libs.coil)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    testImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test"))
    testImplementation(project(":testing"))
    androidTestImplementation(project(":testing"))

    testImplementation(libs.robolectric)
}
