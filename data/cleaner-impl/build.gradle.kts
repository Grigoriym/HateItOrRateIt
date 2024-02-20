plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.cleanerimpl"

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
    implementation(project(":data:cleaner-api"))
    implementation(project(":utils"))
    implementation(project(":data:repo-api"))
    implementation(project(":commons"))
    implementation(project(":domain"))
    implementation(project(":data:db"))

    implementation(libs.timber)

    implementation(libs.androidx.room.runtime)
}
