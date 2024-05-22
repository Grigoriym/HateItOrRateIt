plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
}

android {
    namespace = "com.grappim.hateitorrateit.uikit"
}

dependencies {
    implementation(projects.utils.ui)
    implementation(projects.data.repoApi)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.material.icons.extended)
}
