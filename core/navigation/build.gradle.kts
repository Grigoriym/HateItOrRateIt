plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
}

android {
    namespace = "com.grappim.hateitorrateit.core.navigation"
}

dependencies {
    implementation(libs.androidx.compose.ui.core)
}
