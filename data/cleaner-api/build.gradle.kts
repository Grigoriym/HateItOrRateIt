plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.data.cleanerapi"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
}

dependencies {
    implementation(project(":domain"))
}
