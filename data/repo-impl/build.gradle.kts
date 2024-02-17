plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.android.library.jacoco)
}

android {
    namespace = "com.grappim.hateitorrateit.data.repoimpl"

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
    implementation(project(":utils"))
    implementation(project(":commons"))
    implementation(project(":data:repo-api"))
    implementation(project(":data:db"))
    implementation(project(":data:local-datastorage-api"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.sqlite)

    implementation(libs.timber)
}
