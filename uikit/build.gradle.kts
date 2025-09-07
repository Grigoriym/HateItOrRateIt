plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
}

android {
    namespace = "com.grappim.hateitorrateit.uikit"
}

dependencies {
    implementation(projects.data.repoApi)
    implementation(projects.utils.ui)
    implementation(projects.strings)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
}
