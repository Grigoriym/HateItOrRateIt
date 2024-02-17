plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.android.library.jacoco)
}

android {
    namespace = "com.grappim.hateitorrateit.data.localdatastorageimpl"

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
    implementation(project(":domain"))
    implementation(project(":data:local-datastorage-api"))

    implementation(libs.androidx.datastore.prefs)
    implementation(libs.timber)
}
