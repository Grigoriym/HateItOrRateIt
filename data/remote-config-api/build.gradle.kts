plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.data.remoteconfigapi"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
