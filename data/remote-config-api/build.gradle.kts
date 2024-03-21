plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.data.remoteconfigapi"

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
    implementation(libs.kotlinx.coroutines.core)
}
