plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.grappim.hateitorrateit.di"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":data:worker-api"))
    implementation(project(":data:worker-impl"))
    implementation(project(":data:repo-api"))
    implementation(project(":data:repo-impl"))
    implementation(project(":data:local-datastorage-api"))
    implementation(project(":data:local-datastorage-impl"))
    implementation(project(":data:cleaner-api"))
    implementation(project(":data:cleaner-impl"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}