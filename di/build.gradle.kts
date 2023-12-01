plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.di"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
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
    implementation(project(":analytics-api"))
    implementation(project(":analytics-impl"))
}
